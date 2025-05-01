package com.example.btl_android_project.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.btl_android_project.local.entity.DiaryRecipeCrossRef

@Dao
interface DiaryRecipeCrossRefDao {
    @Query("SELECT * FROM diary_recipe_cross_ref")
    suspend fun getDiaryRecipeCrossRefs(): List<DiaryRecipeCrossRef>

    @Query("SELECT * FROM diary_recipe_cross_ref WHERE diaryId = :diaryId")
    suspend fun getDiaryRecipeCrossRefsByDiaryId(diaryId: Int): List<DiaryRecipeCrossRef>

    @Query("SELECT * FROM diary_recipe_cross_ref WHERE diaryId = :diaryId AND recipeId = :recipeId LIMIT 1")
    suspend fun getDiaryRecipeCrossRef(diaryId: Int, recipeId: Int): DiaryRecipeCrossRef?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaryRecipeCrossRef(crossRef: DiaryRecipeCrossRef): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDiaryRecipeCrossRefs(crossRefs: List<DiaryRecipeCrossRef>)

    @Update
    suspend fun updateDiaryRecipeCrossRef(crossRef: DiaryRecipeCrossRef): Int

    @Delete
    suspend fun deleteDiaryRecipeCrossRef(crossRef: DiaryRecipeCrossRef): Int

    @Query("DELETE FROM diary_recipe_cross_ref WHERE diaryId = :diaryId")
    suspend fun deleteDiaryRecipeCrossRefsByDiaryId(diaryId: Int): Int

    @Query("DELETE FROM diary_recipe_cross_ref")
    suspend fun deleteAllDiaryRecipeCrossRefs(): Int
}