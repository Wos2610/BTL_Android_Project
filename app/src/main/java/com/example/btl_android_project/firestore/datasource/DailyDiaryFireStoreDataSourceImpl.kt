package com.example.btl_android_project.firestore.datasource

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.btl_android_project.local.entity.DailyDiary
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DailyDiaryFireStoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
){
    companion object {
        private const val COLLECTION_NAME = "daily_diaries"
    }

    suspend fun getDailyDiaries(): List<DailyDiary> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME).get().await()
            mapSnapshotToDailyDiaries(snapshot)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getDailyDiariesByUserId(userId: String): List<DailyDiary> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .get()
                .await()
            mapSnapshotToDailyDiaries(snapshot)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getDailyDiaryById(id: String): DailyDiary? {
        return try {
            val document = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("id", id)
                .get()
                .await()
                .documents
                .firstOrNull()

            document?.let { mapDocumentToDailyDiary(it.data) }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getDailyDiaryByDate(userId: String, date: LocalDate): DailyDiary? {
        return try {
            val document = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .whereEqualTo("logDate", date.toString())
                .get()
                .await()
                .documents
                .firstOrNull()

            document?.let { mapDocumentToDailyDiary(it.data) }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun insertDailyDiary(dailyDiary: DailyDiary): String {
        return suspendCoroutine { continuation ->
            val docRef = firestore.collection(COLLECTION_NAME).document()

            val newId = docRef.id

            val updatedDiary = dailyDiary.copy(id = newId)

            val diaryMap = mapOf(
                "id" to newId,  // Sử dụng ID mới
                "userId" to updatedDiary.userId,
                "logDate" to updatedDiary.logDate?.toString(),
                "caloriesRemaining" to updatedDiary.caloriesRemaining,
                "totalFoodCalories" to updatedDiary.totalFoodCalories,
                "totalExerciseCalories" to updatedDiary.totalExerciseCalories,
                "totalWaterMl" to updatedDiary.totalWaterMl,
                "totalFat" to updatedDiary.totalFat,
                "totalCarbs" to updatedDiary.totalCarbs,
                "totalProtein" to updatedDiary.totalProtein,
                "caloriesGoal" to updatedDiary.caloriesGoal
            )

            docRef.set(diaryMap)
                .addOnSuccessListener {
                    continuation.resume(newId)  // Trả về ID mới dạng String
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    suspend fun updateDailyDiary(dailyDiary: DailyDiary): Int {
        return suspendCoroutine { continuation ->
            val diaryMap = mapOf(
                "id" to dailyDiary.id,
                "userId" to dailyDiary.userId,
                "logDate" to dailyDiary.logDate?.toString(),
                "caloriesRemaining" to dailyDiary.caloriesRemaining,
                "totalFoodCalories" to dailyDiary.totalFoodCalories,
                "totalExerciseCalories" to dailyDiary.totalExerciseCalories,
                "totalWaterMl" to dailyDiary.totalWaterMl,
                "totalFat" to dailyDiary.totalFat,
                "totalCarbs" to dailyDiary.totalCarbs,
                "totalProtein" to dailyDiary.totalProtein,
                "caloriesGoal" to dailyDiary.caloriesGoal
            )

            firestore.collection(COLLECTION_NAME)
                .document(dailyDiary.id)
                .set(diaryMap)
                .addOnSuccessListener {
                    continuation.resume(1) // 1 row updated
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    suspend fun deleteDailyDiary(dailyDiary: DailyDiary): Int {
        return suspendCoroutine { continuation ->
            firestore.collection(COLLECTION_NAME)
                .document(dailyDiary.id)
                .delete()
                .addOnSuccessListener {
                    continuation.resume(1) // 1 row deleted
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    private fun mapSnapshotToDailyDiaries(snapshot: QuerySnapshot): List<DailyDiary> {
        return snapshot.documents.mapNotNull { document ->
            mapDocumentToDailyDiary(document.data)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun mapDocumentToDailyDiary(data: Map<String, Any>?): DailyDiary? {
        if (data == null) return null

        return try {
            DailyDiary(
                id = (data["id"] as? String) ?: "",
                userId = (data["userId"] as? String) ?: "",
                logDate = (data["logDate"] as? String)?.let { LocalDate.parse(it) },
                caloriesRemaining = (data["caloriesRemaining"] as? Number)?.toFloat() ?: 0f,
                totalFoodCalories = (data["totalFoodCalories"] as? Number)?.toFloat() ?: 0f,
                totalExerciseCalories = (data["totalExerciseCalories"] as? Number)?.toFloat() ?: 0f,
                totalWaterMl = (data["totalWaterMl"] as? Number)?.toInt() ?: 0,
                totalFat = (data["totalFat"] as? Number)?.toFloat() ?: 0f,
                totalCarbs = (data["totalCarbs"] as? Number)?.toFloat() ?: 0f,
                totalProtein = (data["totalProtein"] as? Number)?.toFloat() ?: 0f,
                caloriesGoal = (data["caloriesGoal"] as? Number)?.toFloat() ?: 0f
            )
        } catch (e: Exception) {
            null
        }
    }
}
