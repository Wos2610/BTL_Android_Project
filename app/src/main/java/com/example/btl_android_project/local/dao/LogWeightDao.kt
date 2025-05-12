package com.example.btl_android_project.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.btl_android_project.local.entity.LogWeight
import kotlinx.coroutines.flow.Flow

@Dao
interface LogWeightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogWeight(logWeight: LogWeight)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLogWeights(logWeights: List<LogWeight>)

    @Update
    suspend fun updateLogWeight(logWeight: LogWeight)

    @Delete
    suspend fun deleteLogWeight(logWeight: LogWeight)

    @Query("SELECT * FROM log_weight WHERE id = :id LIMIT 1")
    suspend fun getLogWeightById(id: String): LogWeight?

    @Query("SELECT * FROM log_weight WHERE userId = :userId ORDER BY date DESC")
    fun getAllLogWeightsByUser(userId: String): Flow<List<LogWeight>>

    @Query("DELETE FROM log_weight WHERE id = :id")
    suspend fun deleteLogWeightById(id: String)

    @Query("SELECT * FROM log_weight WHERE userId = :userId AND date = :date LIMIT 1")
    suspend fun getLogWeightByUserAndDate(userId: String, date: String): LogWeight?

    @Query("SELECT * FROM log_weight WHERE userId = :userId ORDER BY date DESC LIMIT 5")
    suspend fun getLast5Weights(userId: String): List<LogWeight>

}
