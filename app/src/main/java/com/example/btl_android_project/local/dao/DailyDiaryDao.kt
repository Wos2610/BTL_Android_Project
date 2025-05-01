package com.example.btl_android_project.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.btl_android_project.local.entity.DailyDiary
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DailyDiaryDao {
    @Query("SELECT * FROM daily_diary ORDER BY logDate DESC")
    fun getDailyDiaries(): Flow<List<DailyDiary>>

    @Query("SELECT * FROM daily_diary WHERE userId = :userId ORDER BY logDate DESC")
    fun getDailyDiariesByUserId(userId: Int): Flow<List<DailyDiary>>

    @Query("SELECT * FROM daily_diary WHERE id = :id")
    suspend fun getDailyDiaryById(id: Int): DailyDiary?

    @Query("SELECT * FROM daily_diary WHERE userId = :userId AND logDate = :date LIMIT 1")
    suspend fun getDailyDiaryByDate(userId: Int, date: LocalDate): DailyDiary?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyDiary(dailyDiary: DailyDiary): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDailyDiaries(diaries: List<DailyDiary>)

    @Update
    suspend fun updateDailyDiary(dailyDiary: DailyDiary): Int

    @Delete
    suspend fun deleteDailyDiary(dailyDiary: DailyDiary): Int
}
