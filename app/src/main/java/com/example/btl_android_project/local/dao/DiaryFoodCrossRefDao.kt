package com.example.btl_android_project.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.btl_android_project.local.entity.DiaryFoodCrossRef

@Dao
interface DiaryFoodCrossRefDao {
    @Query("SELECT * FROM diary_food_cross_ref")
    suspend fun getDiaryFoodCrossRefs(): List<DiaryFoodCrossRef>

    @Query("SELECT * FROM diary_food_cross_ref WHERE diaryId = :diaryId")
    suspend fun getDiaryFoodCrossRefsByDiaryId(diaryId: Int): List<DiaryFoodCrossRef>

    @Query("SELECT * FROM diary_food_cross_ref WHERE diaryId = :diaryId AND foodId = :foodId LIMIT 1")
    suspend fun getDiaryFoodCrossRef(diaryId: Int, foodId: Int): DiaryFoodCrossRef?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaryFoodCrossRef(crossRef: DiaryFoodCrossRef): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDiaryFoodCrossRefs(crossRefs: List<DiaryFoodCrossRef>)

    @Update
    suspend fun updateDiaryFoodCrossRef(crossRef: DiaryFoodCrossRef): Int

    @Delete
    suspend fun deleteDiaryFoodCrossRef(crossRef: DiaryFoodCrossRef): Int

    @Query("DELETE FROM diary_food_cross_ref WHERE diaryId = :diaryId")
    suspend fun deleteDiaryFoodCrossRefsByDiaryId(diaryId: Int): Int

    @Query("DELETE FROM diary_food_cross_ref")
    suspend fun deleteAllDiaryFoodCrossRefs(): Int
}
