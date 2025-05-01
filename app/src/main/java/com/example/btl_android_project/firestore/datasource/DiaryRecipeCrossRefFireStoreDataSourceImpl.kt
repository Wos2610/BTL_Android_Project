package com.example.btl_android_project.firestore.datasource

import com.example.btl_android_project.local.MealType
import com.example.btl_android_project.local.entity.DiaryRecipeCrossRef
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DiaryRecipeCrossRefFireStoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
){
    
    companion object {
        private const val COLLECTION_NAME = "diary_recipe_cross_refs"
    }
    
    suspend fun getDiaryRecipeCrossRefs(): List<DiaryRecipeCrossRef> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME).get().await()
            mapSnapshotToDiaryRecipeCrossRefs(snapshot)
        } catch (e: Exception) {
            throw e
        }
    }
    
    suspend fun getDiaryRecipeCrossRefsByDiaryId(diaryId: Int): List<DiaryRecipeCrossRef> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("diaryId", diaryId)
                .get()
                .await()
            mapSnapshotToDiaryRecipeCrossRefs(snapshot)
        } catch (e: Exception) {
            throw e
        }
    }
    
    suspend fun insertDiaryRecipeCrossRef(crossRef: DiaryRecipeCrossRef): Long {
        return suspendCoroutine { continuation ->
            val crossRefMap = mapOf(
                "diaryId" to crossRef.diaryId,
                "recipeId" to crossRef.recipeId,
                "servings" to crossRef.servings,
                "mealType" to crossRef.mealType?.name,
                "userId" to crossRef.userId
            )
            
            val documentId = "${crossRef.diaryId}_${crossRef.recipeId}"
            
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
    
    suspend fun updateDiaryRecipeCrossRef(crossRef: DiaryRecipeCrossRef): Int {
        return suspendCoroutine { continuation ->
            val crossRefMap = mapOf(
                "diaryId" to crossRef.diaryId,
                "recipeId" to crossRef.recipeId,
                "servings" to crossRef.servings,
                "mealType" to crossRef.mealType?.name,
                "userId" to crossRef.userId
            )
            
            val documentId = "${crossRef.diaryId}_${crossRef.recipeId}"
            
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
    
    suspend fun deleteDiaryRecipeCrossRef(crossRef: DiaryRecipeCrossRef): Int {
        return suspendCoroutine { continuation ->
            val documentId = "${crossRef.diaryId}_${crossRef.recipeId}"
            
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
    
    suspend fun deleteDiaryRecipeCrossRefsByDiaryId(diaryId: Int): Int {
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
    
    private fun mapSnapshotToDiaryRecipeCrossRefs(snapshot: QuerySnapshot): List<DiaryRecipeCrossRef> {
        return snapshot.documents.mapNotNull { document ->
            mapDocumentToDiaryRecipeCrossRef(document.data)
        }
    }
    
    private fun mapDocumentToDiaryRecipeCrossRef(data: Map<String, Any>?): DiaryRecipeCrossRef? {
        if (data == null) return null
        
        return try {
            DiaryRecipeCrossRef(
                diaryId = (data["diaryId"] as? Number)?.toInt() ?: 0,
                recipeId = (data["recipeId"] as? Number)?.toInt() ?: 0,
                servings = (data["servings"] as? Number)?.toInt() ?: 1,
                mealType = (data["mealType"] as? String)?.let { MealType.valueOf(it) },
                userId = (data["userId"] as? Number)?.toInt() ?: 0,
            )
        } catch (e: Exception) {
            null
        }
    }
}