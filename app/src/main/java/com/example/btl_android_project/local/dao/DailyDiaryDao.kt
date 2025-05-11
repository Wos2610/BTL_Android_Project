package com.example.btl_android_project.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.btl_android_project.local.entity.DailyDiary
import com.example.btl_android_project.local.entity.DiaryWithAllNutrition
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DailyDiaryDao {
    @Query("SELECT * FROM daily_diary ORDER BY logDate DESC")
    fun getDailyDiaries(): Flow<List<DailyDiary>>

    @Query("SELECT * FROM daily_diary WHERE userId = :userId ORDER BY logDate DESC")
    fun getDailyDiariesByUserId(userId: String): Flow<List<DailyDiary>>

    @Query("SELECT * FROM daily_diary WHERE id = :id")
    suspend fun getDailyDiaryById(id: String): DailyDiary?

    @Query("SELECT * FROM daily_diary WHERE userId = :userId AND logDate = :date LIMIT 1")
    suspend fun getDailyDiaryByDate(userId: String, date: LocalDate): DailyDiary?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyDiary(dailyDiary: DailyDiary): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDailyDiaries(diaries: List<DailyDiary>)

    @Update
    suspend fun updateDailyDiary(dailyDiary: DailyDiary): Int

    @Delete
    suspend fun deleteDailyDiary(dailyDiary: DailyDiary): Int

    @Query("DELETE FROM daily_diary")
    suspend fun deleteAllDailyDiaries(): Int

    @Transaction
    @Query("SELECT * FROM daily_diary WHERE userId = :userId AND logDate = :date LIMIT 1")
    suspend fun getDiaryByDate(userId: String, date: LocalDate): DiaryWithAllNutrition?
}
