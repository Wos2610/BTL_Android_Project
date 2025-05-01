package com.example.btl_android_project.firestore.datasource

import com.example.btl_android_project.local.MealType
import com.example.btl_android_project.local.entity.DiaryMealCrossRef
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class DiaryMealCrossRefFireStoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
){
    
    companion object {
        private const val COLLECTION_NAME = "diary_meal_cross_refs"
    }
    
    suspend fun getDiaryMealCrossRefs(): List<DiaryMealCrossRef> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME).get().await()
            mapSnapshotToDiaryMealCrossRefs(snapshot)
        } catch (e: Exception) {
            throw e
        }
    }
    
    suspend fun getDiaryMealCrossRefsByDiaryId(diaryId: Int): List<DiaryMealCrossRef> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("diaryId", diaryId)
                .get()
                .await()
            mapSnapshotToDiaryMealCrossRefs(snapshot)
        } catch (e: Exception) {
            throw e
        }
    }
    
    suspend fun insertDiaryMealCrossRef(crossRef: DiaryMealCrossRef): Long {
        return suspendCoroutine { continuation ->
            val crossRefMap = mapOf(
                "diaryId" to crossRef.diaryId,
                "mealId" to crossRef.mealId,
                "servings" to crossRef.servings,
                "mealType" to crossRef.mealType?.name
            )
            
            val documentId = "${crossRef.diaryId}_${crossRef.mealId}"
            
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
    
    suspend fun updateDiaryMealCrossRef(crossRef: DiaryMealCrossRef): Int {
        return suspendCoroutine { continuation ->
            val crossRefMap = mapOf(
                "diaryId" to crossRef.diaryId,
                "mealId" to crossRef.mealId,
                "servings" to crossRef.servings,
                "mealType" to crossRef.mealType?.name
            )
            
            val documentId = "${crossRef.diaryId}_${crossRef.mealId}"
            
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
    
    suspend fun deleteDiaryMealCrossRef(crossRef: DiaryMealCrossRef): Int {
        return suspendCoroutine { continuation ->
            val documentId = "${crossRef.diaryId}_${crossRef.mealId}"
            
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
    
    suspend fun deleteDiaryMealCrossRefsByDiaryId(diaryId: Int): Int {
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
    
    private fun mapSnapshotToDiaryMealCrossRefs(snapshot: QuerySnapshot): List<DiaryMealCrossRef> {
        return snapshot.documents.mapNotNull { document ->
            mapDocumentToDiaryMealCrossRef(document.data)
        }
    }
    
    private fun mapDocumentToDiaryMealCrossRef(data: Map<String, Any>?): DiaryMealCrossRef? {
        if (data == null) return null
        
        return try {
            DiaryMealCrossRef(
                diaryId = (data["diaryId"] as? Number)?.toInt() ?: 0,
                mealId = (data["mealId"] as? Number)?.toInt() ?: 0,
                servings = (data["servings"] as? Number)?.toInt() ?: 1,
                mealType = (data["mealType"] as? String)?.let { MealType.valueOf(it) },
                userId = (data["userId"] as? Number)?.toInt() ?: 0,
            )
        } catch (e: Exception) {
            null
        }
    }
}