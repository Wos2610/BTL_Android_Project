package com.example.btl_android_project.firestore.datasource

import com.example.btl_android_project.local.entity.DiaryFoodCrossRef
import com.example.btl_android_project.local.enums.MealType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import timber.log.Timber
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
                    continuation.resume(1L)
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
                    continuation.resume(1)
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
                    continuation.resume(1)
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

    suspend fun deleteByUserIdDiaryIdFoodIdMealType(
        userId: String,
        diaryId: String,
        foodId: String,
        mealType: String
    ): Int {
        return suspendCoroutine { continuation ->
            val documentId = "${diaryId}_${foodId}_${mealType}"

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

    suspend fun updateByUserIdDiaryIdFoodIdMealType(
        userId: String,
        diaryId: String,
        foodId: String,
        mealType: String,
        servings: Int,
    ): Int {
        return suspendCoroutine { continuation ->
            val documentId = "${diaryId}_${foodId}_${mealType}"

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

    suspend fun getDiaryFoodCrossRefByUserId(userId: String): List<DiaryFoodCrossRef> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .get()
                .await()
            mapSnapshotToDiaryFoodCrossRefs(snapshot)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getAllByDiaryIds(diaryIds: List<String>): List<DiaryFoodCrossRef> {
        if (diaryIds.isEmpty()) {
            return emptyList()
        }

        val result = mutableListOf<DiaryFoodCrossRef>()

        // Xử lý theo batch để tránh vượt quá giới hạn truy vấn Firestore
        val batchSize = 10 // Firestore giới hạn tối đa 10 phần tử cho mệnh đề IN
        val startTime = System.currentTimeMillis()

        diaryIds.chunked(batchSize).forEach { diaryIdBatch ->
            Timber.d("Fetching food cross refs for diary batch size: ${diaryIdBatch.size}")

            try {
                val snapshot = firestore.collection(COLLECTION_NAME)
                    .whereIn("diaryId", diaryIdBatch)
                    .get()
                    .await()

                val batchResults = snapshot.documents.mapNotNull {
                    it.toObject(DiaryFoodCrossRef::class.java)
                }

                result.addAll(batchResults)
                Timber.d("Fetched ${batchResults.size} food cross refs for this batch")
            } catch (e: Exception) {
                Timber.e(e, "Error fetching food cross refs for diary batch: ${e.message}")
            }
        }

        val elapsedTime = System.currentTimeMillis() - startTime
        Timber.d("Total diary-food cross refs fetched: ${result.size} in ${elapsedTime}ms")
        return result
    }
}