package com.example.btl_android_project.firestore.datasource

import com.example.btl_android_project.local.entity.DiaryMealCrossRef
import com.example.btl_android_project.local.enums.MealType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import timber.log.Timber
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
    
    suspend fun getDiaryMealCrossRefsByDiaryId(diaryId: String): List<DiaryMealCrossRef> {
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
                "mealType" to crossRef.mealType?.name,
                "userId" to crossRef.userId
            )
            
            val documentId = "${crossRef.diaryId}_${crossRef.mealId}_${crossRef.mealType.name}"
            
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
    
    suspend fun updateDiaryMealCrossRef(crossRef: DiaryMealCrossRef): Int {
        return suspendCoroutine { continuation ->
            val crossRefMap = mapOf(
                "diaryId" to crossRef.diaryId,
                "mealId" to crossRef.mealId,
                "servings" to crossRef.servings,
                "mealType" to crossRef.mealType?.name,
                "userId" to crossRef.userId
            )
            
            val documentId = "${crossRef.diaryId}_${crossRef.mealId}"
            
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
    
    suspend fun deleteDiaryMealCrossRef(crossRef: DiaryMealCrossRef): Int {
        return suspendCoroutine { continuation ->
            val documentId = "${crossRef.diaryId}_${crossRef.mealId}"
            
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
    
    suspend fun deleteDiaryMealCrossRefsByDiaryId(diaryId: String): Int {
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

    suspend fun deleteByUserIdDiaryIdMealIdMealType(
        userId: String,
        diaryId: String,
        mealId: String,
        mealType: String
    ): Int {
        return suspendCoroutine { continuation ->
            val documentId = "${diaryId}_${mealId}_$mealType"

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

    suspend fun updateByUserIdDiaryIdMealIdMealType(
        userId: String,
        diaryId: String,
        mealId: String,
        mealType: String,
        servings: Int
    ): Int {
        return suspendCoroutine { continuation ->
            val documentId = "${diaryId}_${mealId}_$mealType"

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
    
    private fun mapSnapshotToDiaryMealCrossRefs(snapshot: QuerySnapshot): List<DiaryMealCrossRef> {
        return snapshot.documents.mapNotNull { document ->
            mapDocumentToDiaryMealCrossRef(document.data)
        }
    }
    
    private fun mapDocumentToDiaryMealCrossRef(data: Map<String, Any>?): DiaryMealCrossRef? {
        if (data == null) return null
        
        return try {
            DiaryMealCrossRef(
                diaryId = (data["diaryId"] as? String) ?: "",
                mealId = (data["mealId"] as? String) ?: "",
                servings = (data["servings"] as? Number)?.toInt() ?: 1,
                mealType = (data["mealType"] as? String).let { MealType.valueOf(it.toString()) },
                userId = (data["userId"] as? String) ?: "",
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getDiaryMealCrossRefsByUserId(userId: String): List<DiaryMealCrossRef> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .get()
                .await()
            mapSnapshotToDiaryMealCrossRefs(snapshot)
        } catch (e: Exception) {
            throw e
        }
    }

    // DiaryMealCrossRefFireStoreDataSourceImpl
    suspend fun getAllByDiaryIds(diaryIds: List<String>): List<DiaryMealCrossRef> {
        if (diaryIds.isEmpty()) {
            return emptyList()
        }

        val result = mutableListOf<DiaryMealCrossRef>()

        // Xử lý theo batch để tránh vượt quá giới hạn truy vấn Firestore
        val batchSize = 10 // Firestore giới hạn tối đa 10 phần tử cho mệnh đề IN
        val startTime = System.currentTimeMillis()

        diaryIds.chunked(batchSize).forEach { diaryIdBatch ->
            Timber.d("Fetching meal cross refs for diary batch size: ${diaryIdBatch.size}")

            try {
                val snapshot = firestore.collection(COLLECTION_NAME)
                    .whereIn("diaryId", diaryIdBatch)
                    .get()
                    .await()

                val batchResults = snapshot.documents.mapNotNull {
                    it.toObject(DiaryMealCrossRef::class.java)
                }

                result.addAll(batchResults)
                Timber.d("Fetched ${batchResults.size} meal cross refs for this batch")
            } catch (e: Exception) {
                Timber.e(e, "Error fetching meal cross refs for diary batch: ${e.message}")
            }
        }

        val elapsedTime = System.currentTimeMillis() - startTime
        Timber.d("Total diary-meal cross refs fetched: ${result.size} in ${elapsedTime}ms")
        return result
    }
}