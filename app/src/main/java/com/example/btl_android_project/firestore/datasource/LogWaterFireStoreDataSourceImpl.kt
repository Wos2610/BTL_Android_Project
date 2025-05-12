package com.example.btl_android_project.firestore.datasource

import android.util.Log
import com.example.btl_android_project.local.entity.LogWater
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LogWaterFireStoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun addLogWater(logWater: LogWater): String = suspendCoroutine { continuation ->
        Log.d("LogWaterFireStore", "Adding logWater at ${logWater.createdAt}")

        val docRef = firestore.collection(LOG_WATER_COLLECTION).document()
        val newId = docRef.id
        val updatedLog = logWater.copy(id = newId)

        docRef.set(updatedLog)
            .addOnSuccessListener {
                Timber.d("LogWater added with ID: $newId")
                continuation.resume(newId)
            }
            .addOnFailureListener { e ->
                Timber.e("Failed to add LogWater: ${e.message}")
                continuation.resumeWithException(e)
            }
    }

    suspend fun updateLogWater(logWater: LogWater) {
        try {
            val logRef = firestore.collection(LOG_WATER_COLLECTION).document(logWater.id.toString())
            val updated = logWater.copy(updatedAt = System.currentTimeMillis())
            logRef.set(updated).await()
            Timber.d("LogWater updated with ID: ${logWater.id}")
        } catch (e: Exception) {
            Timber.e("Error updating LogWater: ${e.message}")
            throw e
        }
    }

    suspend fun deleteLogWater(logId: String) {
        try {
            firestore.collection(LOG_WATER_COLLECTION).document(logId).delete().await()
            Timber.d("LogWater deleted with ID: $logId")
        } catch (e: Exception) {
            Timber.e("Error deleting LogWater: ${e.message}")
            throw e
        }
    }

    suspend fun getLogWaterById(logId: String): LogWater? {
        return try {
            val document = firestore.collection(LOG_WATER_COLLECTION).document(logId).get().await()
            document.toObject(LogWater::class.java)
        } catch (e: Exception) {
            Timber.e("Error getting LogWater by ID: ${e.message}")
            null
        }
    }

    suspend fun getAllLogsByUser(userId: String): List<LogWater> {
        return try {
            val result = firestore.collection(LOG_WATER_COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .await()
            result.toObjects(LogWater::class.java)
        } catch (e: Exception) {
            Timber.e("Error getting LogWaters by user ID: ${e.message}")
            emptyList()
        }
    }

    companion object {
        private const val LOG_WATER_COLLECTION = "log_water"
    }
}
