package com.example.btl_android_project.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.btl_android_project.local.entity.DiaryRecipeCrossRef

@Dao
interface DiaryRecipeCrossRefDao {
    @Query("SELECT * FROM diary_recipe_cross_ref")
    suspend fun getDiaryRecipeCrossRefs(): List<DiaryRecipeCrossRef>

    @Query("SELECT * FROM diary_recipe_cross_ref WHERE diaryId = :diaryId")
    suspend fun getDiaryRecipeCrossRefsByDiaryId(diaryId: String): List<DiaryRecipeCrossRef>

    @Query("SELECT * FROM diary_recipe_cross_ref WHERE diaryId = :diaryId AND recipeId = :recipeId LIMIT 1")
    suspend fun getDiaryRecipeCrossRef(diaryId: String, recipeId: String): DiaryRecipeCrossRef?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaryRecipeCrossRef(crossRef: DiaryRecipeCrossRef): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDiaryRecipeCrossRefs(crossRefs: List<DiaryRecipeCrossRef>)

    @Update
    suspend fun updateDiaryRecipeCrossRef(crossRef: DiaryRecipeCrossRef): Int

    @Delete
    suspend fun deleteDiaryRecipeCrossRef(crossRef: DiaryRecipeCrossRef): Int

    @Query("DELETE FROM diary_recipe_cross_ref WHERE diaryId = :diaryId")
    suspend fun deleteDiaryRecipeCrossRefsByDiaryId(diaryId: String): Int

    @Query("DELETE FROM diary_recipe_cross_ref")
    suspend fun deleteAllDiaryRecipeCrossRefs(): Int

    @Query("DELETE FROM diary_recipe_cross_ref WHERE diaryId = :diaryId")
    fun deletesByDiaryId(diaryId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(crossRefs: List<DiaryRecipeCrossRef>)

    @Transaction
    suspend fun deleteAndInsertInTransaction(diaryId: String, crossRefs: List<DiaryRecipeCrossRef>) {
        deletesByDiaryId(diaryId)
        if (crossRefs.isNotEmpty()) {
            insertAll(crossRefs)
        }
    }

    @Query("DELETE FROM diary_recipe_cross_ref WHERE userId = :userId AND diaryId = :diaryId AND recipeId = :recipeId AND mealType = :mealType")
    suspend fun deleteByUserIdDiaryIdRecipeIdMealType(
        userId: String,
        diaryId: String,
        recipeId: String,
        mealType: String
    )

    @Query("UPDATE diary_recipe_cross_ref SET servings = :servings WHERE userId = :userId AND diaryId = :diaryId AND recipeId = :recipeId AND mealType = :mealType")
    suspend fun updateByUserIdDiaryIdRecipeIdMealType(
        userId: String,
        diaryId: String,
        recipeId: String,
        mealType: String,
        servings: Int
    )

    @Query("DELETE FROM diary_recipe_cross_ref WHERE diaryId IN (:diaryIds)")
    suspend fun deleteAllForDiaries(diaryIds: List<String>)
}