package com.example.btl_android_project.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.btl_android_project.local.entity.DiaryMealCrossRef

@Dao
interface DiaryMealCrossRefDao {
    @Query("SELECT * FROM diary_meal_cross_ref")
    suspend fun getDiaryMealCrossRefs(): List<DiaryMealCrossRef>

    @Query("SELECT * FROM diary_meal_cross_ref WHERE diaryId = :diaryId")
    suspend fun getDiaryMealCrossRefsByDiaryId(diaryId: Int): List<DiaryMealCrossRef>

    @Query("SELECT * FROM diary_meal_cross_ref WHERE diaryId = :diaryId AND mealId = :mealId LIMIT 1")
    suspend fun getDiaryMealCrossRef(diaryId: Int, mealId: Int): DiaryMealCrossRef?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaryMealCrossRef(crossRef: DiaryMealCrossRef): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDiaryMealCrossRefs(crossRefs: List<DiaryMealCrossRef>)

    @Update
    suspend fun updateDiaryMealCrossRef(crossRef: DiaryMealCrossRef): Int

    @Delete
    suspend fun deleteDiaryMealCrossRef(crossRef: DiaryMealCrossRef): Int

    @Query("DELETE FROM diary_meal_cross_ref WHERE diaryId = :diaryId")
    suspend fun deleteDiaryMealCrossRefsByDiaryId(diaryId: Int): Int
}
