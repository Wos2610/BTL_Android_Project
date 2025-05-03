package com.example.btl_android_project.firestore.datasource

import com.example.btl_android_project.local.MealType
import com.example.btl_android_project.local.entity.DiaryFoodCrossRef
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DiaryFoodCrossRefFireStoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
){
    
    companion object {
        private const val COLLECTION_NAME = "diary_food_cross_refs"
    }
    
    suspend fun getDiaryFoodCrossRefs(): List<DiaryFoodCrossRef> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME).get().await()
            mapSnapshotToDiaryFoodCrossRefs(snapshot)
        } catch (e: Exception) {
            throw e
        }
    }
    
    suspend fun getDiaryFoodCrossRefsByDiaryId(diaryId: String): List<DiaryFoodCrossRef> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("diaryId", diaryId)
                .get()
                .await()
            mapSnapshotToDiaryFoodCrossRefs(snapshot)
        } catch (e: Exception) {
            throw e
        }
    }
    
    suspend fun insertDiaryFoodCrossRef(crossRef: DiaryFoodCrossRef): Long {
        return suspendCoroutine { continuation ->
            val crossRefMap = mapOf(
                "diaryId" to crossRef.diaryId,
                "foodId" to crossRef.foodId,
                "servings" to crossRef.servings,
                "mealType" to crossRef.mealType.name,
                "userId" to crossRef.userId
            )
            
            val documentId = "${crossRef.diaryId}_${crossRef.foodId}_${crossRef.mealType.name}"
            
            firestore.collection(COLLECTION_NAME)
                .document(documentId)
                .set(crossRefMap)
                .addOnSuccessListener {
                    continuation.resume(1L) // Success
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }
    
    suspend fun updateDiaryFoodCrossRef(crossRef: DiaryFoodCrossRef): Int {
        return suspendCoroutine { continuation ->
            val crossRefMap = mapOf(
                "diaryId" to crossRef.diaryId,
                "foodId" to crossRef.foodId,
                "servings" to crossRef.servings,
                "mealType" to crossRef.mealType?.name,
                "userId" to crossRef.userId
            )
            
            val documentId = "${crossRef.diaryId}_${crossRef.foodId}"
            
            firestore.collection(COLLECTION_NAME)
                .document(documentId)
                .set(crossRefMap)
                .addOnSuccessListener {
                    continuation.resume(1) // 1 row updated
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }
    
    suspend fun deleteDiaryFoodCrossRef(crossRef: DiaryFoodCrossRef): Int {
        return suspendCoroutine { continuation ->
            val documentId = "${crossRef.diaryId}_${crossRef.foodId}"
            
            firestore.collection(COLLECTION_NAME)
                .document(documentId)
                .delete()
                .addOnSuccessListener {
                    continuation.resume(1) // 1 row deleted
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }
    
    suspend fun deleteDiaryFoodCrossRefsByDiaryId(diaryId: String): Int {
        return try {
            val batch = firestore.batch()
            var count = 0
            
            val documents = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("diaryId", diaryId)
                .get()
                .await()
                .documents
            
            documents.forEach { document ->
                batch.delete(document.reference)
                count++
            }
            
            if (count > 0) {
                batch.commit().await()
            }
            
            count
        } catch (e: Exception) {
            throw e
        }
    }
    
    private fun mapSnapshotToDiaryFoodCrossRefs(snapshot: QuerySnapshot): List<DiaryFoodCrossRef> {
        return snapshot.documents.mapNotNull { document ->
            mapDocumentToDiaryFoodCrossRef(document.data)
        }
    }
    
    private fun mapDocumentToDiaryFoodCrossRef(data: Map<String, Any>?): DiaryFoodCrossRef? {
        if (data == null) return null
        
        return try {
            DiaryFoodCrossRef(
                diaryId = (data["diaryId"] as? String) ?: "",
                foodId = (data["foodId"] as? String) ?: "",
                servings = (data["servings"] as? Number)?.toInt() ?: 1,
                mealType = (data["mealType"] as? String).let { MealType.valueOf(it.toString()) },
                userId = (data["userId"] as? String) ?: "",
            )
        } catch (e: Exception) {
            null
        }
    }
}