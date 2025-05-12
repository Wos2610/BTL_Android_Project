package com.example.btl_android_project.repository

import com.example.btl_android_project.firestore.datasource.LogWaterFireStoreDataSourceImpl
import com.example.btl_android_project.local.dao.LogWaterDao
import com.example.btl_android_project.local.entity.LogWater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class LogWaterRepository @Inject constructor(
    private val logWaterDao: LogWaterDao,
    private val logWaterFireStoreDataSource: LogWaterFireStoreDataSourceImpl
) {
    suspend fun addLogWater(logWater: LogWater): String {
        return try {
            val firestoreId = logWaterFireStoreDataSource.addLogWater(logWater)
            val updatedLog = logWater.copy(id = firestoreId)
            logWaterDao.insertLogWater(updatedLog)
            Timber.d("LogWater inserted with ID: $firestoreId")
            firestoreId
        } catch (e: Exception) {
            Timber.e("Error adding LogWater: ${e.message}")
            throw e
        }
    }

    suspend fun updateLogWater(logWater: LogWater) {
        try {
            logWaterDao.updateLogWater(logWater)
            logWaterFireStoreDataSource.updateLogWater(logWater)
            Timber.d("LogWater updated with ID: ${logWater.id}")
        } catch (e: Exception) {
            Timber.e("Error updating LogWater: ${e.message}")
            throw e
        }
    }

    suspend fun deleteLogWater(logWater: LogWater) {
        try {
            logWaterDao.deleteLogWater(logWater)
            logWaterFireStoreDataSource.deleteLogWater(logWater.id.toString())
            Timber.d("LogWater deleted with ID: ${logWater.id}")
        } catch (e: Exception) {
            Timber.e("Error deleting LogWater: ${e.message}")
            throw e
        }
    }

    suspend fun getLogWaterById(logId: String): LogWater? {
        return try {
            val local = logWaterDao.getLogWaterById(logId)
            if (local != null) {
                local
            } else {
                val remote = logWaterFireStoreDataSource.getLogWaterById(logId)
                if (remote != null) logWaterDao.insertLogWater(remote)
                remote
            }
        } catch (e: Exception) {
            Timber.e("Error getting LogWater by ID: ${e.message}")
            null
        }
    }

    fun getAllLogWatersByUser(userId: String): Flow<List<LogWater>> {
        return try {
            logWaterDao.getAllLogWatersByUser(userId)
        } catch (e: Exception) {
            Timber.e("Error getting LogWater list: ${e.message}")
            flowOf(emptyList())
        }
    }

    suspend fun syncLogsFromFirestore(userId: String) {
        try {
            val firestoreLogs = logWaterFireStoreDataSource.getAllLogsByUser(userId)
            logWaterDao.insertAllLogWaters(firestoreLogs)
            Timber.d("Synced ${firestoreLogs.size} LogWater entries from Firestore")
        } catch (e: Exception) {
            Timber.e("Error syncing LogWater entries: ${e.message}")
        }
    }

    suspend fun getLogWaterByDailyDiaryId(dailyDiaryId: String): List<LogWater>? {
        return withContext(Dispatchers.IO) {
            logWaterDao.getLogWaterByDailyDiaryId(dailyDiaryId)
        }
    }
}
