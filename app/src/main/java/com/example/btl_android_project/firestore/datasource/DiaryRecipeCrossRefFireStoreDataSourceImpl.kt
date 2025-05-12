package com.example.btl_android_project.firestore.datasource

import com.example.btl_android_project.local.entity.DiaryRecipeCrossRef
import com.example.btl_android_project.local.enums.MealType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import timber.log.Timber
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
    
    suspend fun getDiaryRecipeCrossRefsByDiaryId(diaryId: String): List<DiaryRecipeCrossRef> {
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
            
            val documentId = "${crossRef.diaryId}_${crossRef.recipeId}_${crossRef.mealType.name}"
            
            firestore.collection(COLLECTION_NAME)
                .document(documentId)
                .set(crossRefMap)
                .addOnSuccessListener {
                    continuation.resume(1L)
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
                    continuation.resume(1)
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
                    continuation.resume(1)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }
    
    suspend fun deleteDiaryRecipeCrossRefsByDiaryId(diaryId: String): Int {
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

    suspend fun deleteByUserIdDiaryIdRecipeIdMealType(
        userId: String,
        diaryId: String,
        recipeId: String,
        mealType: String,
    ): Int {

        return suspendCoroutine { continuation ->
            val documentId = "${diaryId}_${recipeId}_$mealType"

            firestore.collection(COLLECTION_NAME)
                .document(documentId)
                .delete()
                .addOnSuccessListener {
                    continuation.resume(1)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    suspend fun updateByUserIdDiaryIdRecipeIdMealType(
        userId: String,
        diaryId: String,
        recipeId: String,
        mealType: String,
        servings: Int,
    ): Int {
        return suspendCoroutine { continuation ->
            val documentId = "${diaryId}_${recipeId}_$mealType"

            firestore.collection(COLLECTION_NAME)
                .document(documentId)
                .update("servings", servings)
                .addOnSuccessListener {
                    continuation.resume(1)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
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
                diaryId = (data["diaryId"] as? String) ?: "",
                recipeId = (data["recipeId"] as? String) ?: "",
                servings = (data["servings"] as? Number)?.toInt() ?: 1,
                mealType = (data["mealType"] as? String).let { MealType.valueOf(it.toString()) },
                userId = (data["userId"] as? String) ?: "",
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getDiaryRecipeCrossRefsByUserId(userId: String): List<DiaryRecipeCrossRef> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .get()
                .await()
            mapSnapshotToDiaryRecipeCrossRefs(snapshot)
        } catch (e: Exception) {
            throw e
        }
    }

    // DiaryRecipeCrossRefFireStoreDataSourceImpl
    suspend fun getAllByDiaryIds(diaryIds: List<String>): List<DiaryRecipeCrossRef> {
        if (diaryIds.isEmpty()) {
            return emptyList()
        }

        val result = mutableListOf<DiaryRecipeCrossRef>()

        // Xử lý theo batch để tránh vượt quá giới hạn truy vấn Firestore
        val batchSize = 10 // Firestore giới hạn tối đa 10 phần tử cho mệnh đề IN
        val startTime = System.currentTimeMillis()

        diaryIds.chunked(batchSize).forEach { diaryIdBatch ->
            Timber.d("Fetching recipe cross refs for diary batch size: ${diaryIdBatch.size}")

            try {
                val snapshot = firestore.collection(COLLECTION_NAME)
                    .whereIn("diaryId", diaryIdBatch)
                    .get()
                    .await()

                val batchResults = snapshot.documents.mapNotNull {
                    it.toObject(DiaryRecipeCrossRef::class.java)
                }

                result.addAll(batchResults)
                Timber.d("Fetched ${batchResults.size} recipe cross refs for this batch")
            } catch (e: Exception) {
                Timber.e(e, "Error fetching recipe cross refs for diary batch: ${e.message}")
            }
        }

        val elapsedTime = System.currentTimeMillis() - startTime
        Timber.d("Total diary-recipe cross refs fetched: ${result.size} in ${elapsedTime}ms")
        return result
    }
}