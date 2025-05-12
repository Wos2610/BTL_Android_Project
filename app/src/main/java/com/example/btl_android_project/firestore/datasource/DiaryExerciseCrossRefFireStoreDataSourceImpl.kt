package com.example.btl_android_project.firestore.datasource

import com.example.btl_android_project.local.entity.DiaryExerciseCrossRef
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DiaryExerciseCrossRefFireStoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    companion object {
        private const val COLLECTION_NAME = "diary_exercise_cross_refs"
    }

    suspend fun getDiaryExerciseCrossRefs(): List<DiaryExerciseCrossRef> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME).get().await()
            mapSnapshotToDiaryExerciseCrossRefs(snapshot)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getDiaryExerciseCrossRefsByDiaryId(diaryId: String): List<DiaryExerciseCrossRef> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("diaryId", diaryId)
                .get()
                .await()
            mapSnapshotToDiaryExerciseCrossRefs(snapshot)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun insertDiaryExerciseCrossRef(crossRef: DiaryExerciseCrossRef): Long {
        return suspendCoroutine { continuation ->
            val crossRefMap = mapOf(
                "diaryId" to crossRef.diaryId,
                "exerciseId" to crossRef.exerciseId,
                "userId" to crossRef.userId,
                "servings" to crossRef.servings,
            )

            val documentId = "${crossRef.diaryId}_${crossRef.exerciseId}"

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

    suspend fun updateDiaryExerciseCrossRef(crossRef: DiaryExerciseCrossRef): Int {
        return suspendCoroutine { continuation ->
            val crossRefMap = mapOf(
                "diaryId" to crossRef.diaryId,
                "exerciseId" to crossRef.exerciseId,
                "userId" to crossRef.userId,
                "servings" to crossRef.servings,
            )

            val documentId = "${crossRef.diaryId}_${crossRef.exerciseId}"

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

    suspend fun deleteDiaryExerciseCrossRef(crossRef: DiaryExerciseCrossRef): Int {
        return suspendCoroutine { continuation ->
            val documentId = "${crossRef.diaryId}_${crossRef.exerciseId}"

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

    suspend fun deleteDiaryExerciseCrossRefsByDiaryId(diaryId: String): Int {
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

    suspend fun deleteByUserIdDiaryIdExerciseId(
        userId: String,
        diaryId: String,
        exerciseId: Int
    ): Int {
        return try {
            val documents = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .whereEqualTo("diaryId", diaryId)
                .whereEqualTo("exerciseId", exerciseId)
                .get()
                .await()
                .documents

            if (documents.isEmpty()) return 0

            val batch = firestore.batch()
            documents.forEach { document ->
                batch.delete(document.reference)
            }

            batch.commit().await()
            documents.size
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateByUserIdDiaryIdExerciseId(
        userId: String,
        diaryId: String,
        exerciseId: Int,
        minutes: Int,
        totalCalories: Float
    ): Int {
        return try {
            val documents = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .whereEqualTo("diaryId", diaryId)
                .whereEqualTo("exerciseId", exerciseId)
                .get()
                .await()
                .documents

            if (documents.isEmpty()) return 0

            val batch = firestore.batch()
            documents.forEach { document ->
                batch.update(document.reference,
                    mapOf(
                        "minutes" to minutes,
                        "totalCalories" to totalCalories
                    )
                )
            }

            batch.commit().await()
            documents.size
        } catch (e: Exception) {
            throw e
        }
    }

    private fun mapDocumentToDiaryExerciseCrossRef(data: Map<String, Any>?): DiaryExerciseCrossRef? {
        if (data == null) return null

        val diaryId = data["diaryId"] as? String ?: return null
        val exerciseId = data["exerciseId"] as? String ?: return null
        val userId = data["userId"] as? String ?: return null
        val servings = data["servings"] as? Int ?: 1

        return DiaryExerciseCrossRef(
            diaryId = diaryId,
            exerciseId = exerciseId,
            userId = userId,
            servings = servings
        )
    }

    private fun mapSnapshotToDiaryExerciseCrossRefs(snapshot: QuerySnapshot): List<DiaryExerciseCrossRef> {
        return snapshot.documents.mapNotNull { document ->
            mapDocumentToDiaryExerciseCrossRef(document.data)
        }

    }
    suspend fun getDiaryExerciseCrossRefByUserId(userId: String): List<DiaryExerciseCrossRef> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .get()
                .await()
            mapSnapshotToDiaryExerciseCrossRefs(snapshot)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getAllByDiaryIds(diaryIds: List<String>): List<DiaryExerciseCrossRef> {
        if (diaryIds.isEmpty()) {
            return emptyList()
        }

        val result = mutableListOf<DiaryExerciseCrossRef>()

        // Process in batches to avoid exceeding Firestore query limits
        val batchSize = 10 // Firestore limits IN clauses to 10 elements
        val startTime = System.currentTimeMillis()

        diaryIds.chunked(batchSize).forEach { diaryIdBatch ->
            Timber.d("Fetching exercise cross refs for diary batch size: ${diaryIdBatch.size}")

            try {
                val snapshot = firestore.collection(COLLECTION_NAME)
                    .whereIn("diaryId", diaryIdBatch)
                    .get()
                    .await()

                val batchResults = snapshot.documents.mapNotNull {
                    it.toObject(DiaryExerciseCrossRef::class.java)
                }

                result.addAll(batchResults)
                Timber.d("Fetched ${batchResults.size} exercise cross refs for this batch")
            } catch (e: Exception) {
                Timber.e(e, "Error fetching exercise cross refs for diary batch: ${e.message}")
            }
        }

        val elapsedTime = System.currentTimeMillis() - startTime
        Timber.d("Total diary-exercise cross refs fetched: ${result.size} in ${elapsedTime}ms")
        return result
    }


}