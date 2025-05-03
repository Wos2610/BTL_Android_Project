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
    suspend fun getDiaryFoodCrossRefsByDiaryId(diaryId: String): List<DiaryFoodCrossRef>

    @Query("SELECT * FROM diary_food_cross_ref WHERE diaryId = :diaryId AND foodId = :foodId")
    suspend fun getDiaryFoodCrossRef(diaryId: String, foodId: String): List<DiaryFoodCrossRef>?

    @Query("SELECT * FROM diary_food_cross_ref WHERE diaryId = :diaryId AND foodId = :foodId AND mealType = :mealType LIMIT 1")
    suspend fun getDiaryFoodCrossRefByMealType(diaryId: String, foodId: String, mealType: String): DiaryFoodCrossRef?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaryFoodCrossRef(crossRef: DiaryFoodCrossRef): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDiaryFoodCrossRefs(crossRefs: List<DiaryFoodCrossRef>)

    @Update
    suspend fun updateDiaryFoodCrossRef(crossRef: DiaryFoodCrossRef)

    @Delete
    suspend fun deleteDiaryFoodCrossRef(crossRef: DiaryFoodCrossRef)

    @Query("DELETE FROM diary_food_cross_ref WHERE diaryId = :diaryId")
    suspend fun deleteDiaryFoodCrossRefsByDiaryId(diaryId: String): Int

    @Query("DELETE FROM diary_food_cross_ref")
    suspend fun deleteAllDiaryFoodCrossRefs()
}
