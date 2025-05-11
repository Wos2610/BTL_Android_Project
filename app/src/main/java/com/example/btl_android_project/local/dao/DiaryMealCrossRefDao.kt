package com.example.btl_android_project.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.btl_android_project.local.entity.DiaryMealCrossRef

@Dao
interface DiaryMealCrossRefDao {
    @Query("SELECT * FROM diary_meal_cross_ref")
    suspend fun getDiaryMealCrossRefs(): List<DiaryMealCrossRef>

    @Query("SELECT * FROM diary_meal_cross_ref WHERE diaryId = :diaryId")
    suspend fun getDiaryMealCrossRefsByDiaryId(diaryId: String): List<DiaryMealCrossRef>

    @Query("SELECT * FROM diary_meal_cross_ref WHERE diaryId = :diaryId AND mealId = :mealId LIMIT 1")
    suspend fun getDiaryMealCrossRef(diaryId: String, mealId: String): DiaryMealCrossRef?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaryMealCrossRef(crossRef: DiaryMealCrossRef): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDiaryMealCrossRefs(crossRefs: List<DiaryMealCrossRef>)

    @Update
    suspend fun updateDiaryMealCrossRef(crossRef: DiaryMealCrossRef): Int

    @Delete
    suspend fun deleteDiaryMealCrossRef(crossRef: DiaryMealCrossRef): Int

    @Query("DELETE FROM diary_meal_cross_ref WHERE diaryId = :diaryId")
    suspend fun deleteDiaryMealCrossRefsByDiaryId(diaryId: String): Int

    @Query("DELETE FROM diary_meal_cross_ref")
    suspend fun deleteAllDiaryMealCrossRefs(): Int

    @Query("DELETE FROM diary_meal_cross_ref WHERE diaryId = :diaryId")
    fun deletesByDiaryId(diaryId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(crossRefs: List<DiaryMealCrossRef>)


    @Transaction
    suspend fun deleteAndInsertInTransaction(diaryId: String, crossRefs: List<DiaryMealCrossRef>) {
        deletesByDiaryId(diaryId)
        if (crossRefs.isNotEmpty()) {
            insertAll(crossRefs)
        }
    }

    @Query("DELETE FROM diary_meal_cross_ref WHERE userId = :userId AND diaryId = :diaryId AND mealId = :mealId AND mealType = :mealType")
    suspend fun deleteByUserIdDiaryIdMealIdMealType(
        userId: String,
        diaryId: String,
        mealId: String,
        mealType: String
    ): Int

    @Query("UPDATE diary_meal_cross_ref SET servings = :servings WHERE userId = :userId AND diaryId = :diaryId AND mealId = :mealId AND mealType = :mealType")
    suspend fun updateByUserIdDiaryIdMealIdMealType(
        userId: String,
        diaryId: String,
        mealId: String,
        mealType: String,
        servings: Int
    ): Int
}
