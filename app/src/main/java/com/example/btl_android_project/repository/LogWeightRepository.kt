package com.example.btl_android_project.repository

import com.example.btl_android_project.firestore.datasource.LogWeightFireStoreDataSourceImpl
import com.example.btl_android_project.local.dao.LogWeightDao
import com.example.btl_android_project.local.entity.LogWeight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber
import javax.inject.Inject

class LogWeightRepository @Inject constructor(
    private val logWeightDao: LogWeightDao,
    private val logWeightFireStoreDataSource: LogWeightFireStoreDataSourceImpl
) {

    suspend fun insertLogWeight(logWeight: LogWeight): String {
        return try {
            val firestoreId = logWeightFireStoreDataSource.addLogWeight(logWeight)
            val updatedLogWeight = logWeight.copy(id = firestoreId)
            logWeightDao.insertLogWeight(updatedLogWeight)

            Timber.d("LogWeight inserted with ID: $firestoreId")
            firestoreId
        } catch (e: Exception) {
            Timber.e("Error inserting log weight: ${e.message}")
            throw e
        }
    }

    suspend fun getLast5Weights(userId: String): List<LogWeight> {
        return logWeightDao.getLast5Weights(userId)
    }


    suspend fun updateLogWeight(logWeight: LogWeight) {
        try {
            logWeightDao.updateLogWeight(logWeight)
            logWeightFireStoreDataSource.updateLogWeight(logWeight)

            Timber.d("LogWeight updated with ID: ${logWeight.id}")
        } catch (e: Exception) {
            Timber.e("Error updating log weight: ${e.message}")
            throw e
        }
    }

    suspend fun deleteLogWeight(logWeight: LogWeight) {
        try {
            logWeightDao.deleteLogWeight(logWeight)
            logWeightFireStoreDataSource.deleteLogWeight(logWeight.id)

            Timber.d("LogWeight deleted with ID: ${logWeight.id}")
        } catch (e: Exception) {
            Timber.e("Error deleting log weight: ${e.message}")
            throw e
        }
    }

    suspend fun getLogWeightById(id: String): LogWeight? {
        return try {
            logWeightDao.getLogWeightById(id)
                ?: logWeightFireStoreDataSource.getLogWeightById(id)?.also {
                    logWeightDao.insertLogWeight(it)
                }
        } catch (e: Exception) {
            Timber.e("Error getting log weight by ID: ${e.message}")
            null
        }
    }

    fun getAllLogWeightsByUser(userId: String): Flow<List<LogWeight>> {
        return try {
            logWeightDao.getAllLogWeightsByUser(userId)
        } catch (e: Exception) {
            Timber.e("Error getting log weights by user: ${e.message}")
            flowOf(emptyList())
        }
    }

    suspend fun syncLogWeightsFromFirestore(userId: String) {
        try {
            val firestoreLogs = logWeightFireStoreDataSource.getAllLogWeightsByUser(userId)
            logWeightDao.insertAllLogWeights(firestoreLogs)
            Timber.d("Synced ${firestoreLogs.size} log weights from Firestore")
        } catch (e: Exception) {
            Timber.e("Error syncing log weights: ${e.message}")
        }
    }

    suspend fun getLogWeightByUserAndDate(userId: String, date: String): LogWeight? {
        return try {
            logWeightDao.getLogWeightByUserAndDate(userId, date)
        } catch (e: Exception) {
            Timber.e("Error getting log weight by user and date: ${e.message}")
            null
        }
    }
}
