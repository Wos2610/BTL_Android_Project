package com.example.btl_android_project.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.btl_android_project.local.entity.DiaryExerciseCrossRef

@Dao
interface DiaryExerciseCrossRefDao {
    @Query("SELECT * FROM diary_exercise_cross_ref")
    suspend fun getDiaryExerciseCrossRefs(): List<DiaryExerciseCrossRef>

    @Query("SELECT * FROM diary_exercise_cross_ref WHERE diaryId = :diaryId")
    suspend fun getDiaryExerciseCrossRefsByDiaryId(diaryId: String): List<DiaryExerciseCrossRef>

    @Query("SELECT * FROM diary_exercise_cross_ref WHERE diaryId = :diaryId AND exerciseId = :exerciseId")
    suspend fun getDiaryExerciseCrossRef(diaryId: String, exerciseId: String): DiaryExerciseCrossRef ?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaryExerciseCrossRef(crossRef: DiaryExerciseCrossRef): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(crossRefs: List<DiaryExerciseCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDiaryExerciseCrossRefs(crossRefs: List<DiaryExerciseCrossRef>)

    @Update
    suspend fun updateDiaryExerciseCrossRef(crossRef: DiaryExerciseCrossRef)

    @Delete
    suspend fun deleteDiaryExerciseCrossRef(crossRef: DiaryExerciseCrossRef)

    @Query("DELETE FROM diary_exercise_cross_ref WHERE diaryId = :diaryId")
    suspend fun deleteDiaryExerciseCrossRefsByDiaryId(diaryId: String): Int

    @Query("DELETE FROM diary_exercise_cross_ref WHERE diaryId = :diaryId")
    fun deletesByDiaryId(diaryId: String): Int

    @Query("DELETE FROM diary_exercise_cross_ref")
    suspend fun deleteAllDiaryExerciseCrossRefs()

    @Transaction
    suspend fun deleteAndInsertInTransaction(diaryId: String, crossRefs: List<DiaryExerciseCrossRef>) {
        deletesByDiaryId(diaryId)
        if (crossRefs.isNotEmpty()) {
            insertAll(crossRefs)
        }
    }

    @Query("SELECT * FROM diary_exercise_cross_ref WHERE exerciseId = :exerciseId")
    suspend fun getDiaryExerciseCrossRefByExerciseId(exerciseId: Int): List<DiaryExerciseCrossRef>?

    @Query("DELETE FROM diary_exercise_cross_ref WHERE userId = :userId AND diaryId = :diaryId AND exerciseId = :exerciseId")
    suspend fun deleteByUserIdDiaryIdExerciseId(
        userId: String,
        diaryId: String,
        exerciseId: Int
    )

    @Query("UPDATE diary_exercise_cross_ref SET servings = :servings WHERE userId = :userId AND diaryId = :diaryId AND exerciseId = :exerciseId")
    suspend fun updateDiaryExerciseCrossRefByUserIdDiaryIdExerciseId(
        userId: String,
        diaryId: String,
        exerciseId: Int,
        servings: Int,
    )

    @Query("DELETE FROM diary_exercise_cross_ref WHERE diaryId IN (:diaryIds)")
    suspend fun deleteAllForDiaries(diaryIds: List<String>)
}