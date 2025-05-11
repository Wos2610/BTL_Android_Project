package com.example.btl_android_project.local.dao

import androidx.room.*
import com.example.btl_android_project.local.entity.Food
import com.example.btl_android_project.local.entity.LogWater
import kotlinx.coroutines.flow.Flow

@Dao
interface LogWaterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogWater(logWater: LogWater): Long

    @Update
    suspend fun updateLogWater(logWater: LogWater)

    @Delete
    suspend fun deleteLogWater(logWater: LogWater)

    @Query("SELECT * FROM log_water WHERE id = :logWaterId")
    suspend fun getLogWaterById(logWaterId: String): LogWater?

    @Query("SELECT * FROM log_water WHERE userId = :userId")
    fun getAllLogWatersByUser(userId: String): Flow<List<LogWater>>

    @Query("DELETE FROM log_water WHERE id = :logWaterId")
    suspend fun deleteLogWaterById(logWaterId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLogWaters(logWaters: List<LogWater>)
}