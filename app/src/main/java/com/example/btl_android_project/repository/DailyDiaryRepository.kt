package com.example.btl_android_project.repository

import android.util.Log
import com.example.btl_android_project.firestore.datasource.DailyDiaryFireStoreDataSourceImpl
import com.example.btl_android_project.local.dao.DailyDiaryDao
import com.example.btl_android_project.local.entity.DailyDiary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class DailyDiaryRepository @Inject constructor(
    private val dailyDiaryDao: DailyDiaryDao,
    private val diaryFireStoreDataSource: DailyDiaryFireStoreDataSourceImpl
) {
    private val TAG = "DailyDiaryRepository"
    
    /**
     * Get all daily diaries
     */
//    fun getDailyDiaries(): Flow<List<DailyDiary>> = dailyDiaryDao.getDailyDiaries()
    
    /**
     * Get daily diaries for a specific user
     */
    suspend fun getDailyDiariesByUserId(userId: Int): Flow<List<DailyDiary>> {
        return withContext(Dispatchers.IO) {
            dailyDiaryDao.getDailyDiariesByUserId(userId)
        }
    }

    /**
     * Get a daily diary by its ID
     */
    suspend fun getDailyDiaryById(id: Int): DailyDiary? {
        return withContext(Dispatchers.IO) {
            dailyDiaryDao.getDailyDiaryById(id)
        }
    }
    
    /**
     * Get a user's daily diary for a specific date
     */
    suspend fun getDailyDiaryByDate(userId: Int, date: LocalDate): DailyDiary? {
        return withContext(Dispatchers.IO) {
            dailyDiaryDao.getDailyDiaryByDate(userId, date)
        }
    }
    
    /**
     * Create a new daily diary entry
     */
    suspend fun createDailyDiary(
        userId: Int,
        logDate: LocalDate,
        caloriesRemaining: Float,
        totalFoodCalories: Float,
        totalExerciseCalories: Float,
        totalWaterMl: Int
    ): Int {
        return withContext(Dispatchers.IO) {
            val dailyDiary = DailyDiary(
                userId = userId,
                logDate = logDate,
                caloriesRemaining = caloriesRemaining,
                totalFoodCalories = totalFoodCalories,
                totalExerciseCalories = totalExerciseCalories,
                totalWaterMl = totalWaterMl
            )
            
            val diaryId = dailyDiaryDao.insertDailyDiary(dailyDiary).toInt()
            Log.d(TAG, "Inserted daily diary with ID: $diaryId")
            
            // Add to Firestore
            val updatedDiary = dailyDiary.copy(id = diaryId)
            diaryFireStoreDataSource.insertDailyDiary(updatedDiary)
            
            diaryId
        }
    }
    
    /**
     * Update an existing daily diary entry
     */
    suspend fun updateDailyDiary(
        id: Int,
        userId: Int,
        logDate: LocalDate,
        caloriesRemaining: Float,
        totalFoodCalories: Float,
        totalExerciseCalories: Float,
        totalWaterMl: Int
    ) {
        withContext(Dispatchers.IO) {
            val updatedDiary = DailyDiary(
                id = id,
                userId = userId,
                logDate = logDate,
                caloriesRemaining = caloriesRemaining,
                totalFoodCalories = totalFoodCalories,
                totalExerciseCalories = totalExerciseCalories,
                totalWaterMl = totalWaterMl
            )
            
            dailyDiaryDao.updateDailyDiary(updatedDiary)
            Log.d(TAG, "Updated daily diary with ID: $id")
            
            // Update in Firestore
            diaryFireStoreDataSource.updateDailyDiary(updatedDiary)
        }
    }
    
    /**
     * Delete a daily diary entry
     */
    suspend fun deleteDailyDiary(dailyDiary: DailyDiary) {
        withContext(Dispatchers.IO) {
            dailyDiaryDao.deleteDailyDiary(dailyDiary)
            Log.d(TAG, "Deleted daily diary with ID: ${dailyDiary.id}")
            
            // Delete from Firestore
            diaryFireStoreDataSource.deleteDailyDiary(dailyDiary)
        }
    }
    
    /**
     * Pull diary data from Firestore for a specific user
     */
    suspend fun pullFromFireStore(userId: Int) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Pulling daily diaries from Firestore")
            val diaries = diaryFireStoreDataSource.getDailyDiariesByUserId(userId)
            Log.d(TAG, "Pulled ${diaries.size} daily diaries from Firestore")
            dailyDiaryDao.deleteAllDailyDiaries()
            dailyDiaryDao.insertAllDailyDiaries(diaries)
        }
    }
    
    /**
     * Get or create a daily diary for a specific date
     */
    suspend fun getOrCreateDailyDiary(userId: Int, date: LocalDate): DailyDiary {
        return withContext(Dispatchers.IO) {
            val existingDiary = dailyDiaryDao.getDailyDiaryByDate(userId, date)
            
            if (existingDiary != null) {
                existingDiary
            } else {
                // Create a new diary for this date with default values
                val newDiary = DailyDiary(
                    userId = userId,
                    logDate = date,
                    caloriesRemaining = 0f,
                    totalFoodCalories = 0f,
                    totalExerciseCalories = 0f,
                    totalWaterMl = 0
                )
                
                val diaryId = dailyDiaryDao.insertDailyDiary(newDiary).toInt()
                Log.d(TAG, "Created new daily diary with ID: $diaryId for date: $date")
                
                // Add to Firestore
                val completeDiary = newDiary.copy(id = diaryId)
                diaryFireStoreDataSource.insertDailyDiary(completeDiary)
                
                completeDiary
            }
        }
    }
    
    /**
     * Add water consumption to a diary entry
     */
    suspend fun addWater(diaryId: Int, waterMl: Int) {
        withContext(Dispatchers.IO) {
            val diary = dailyDiaryDao.getDailyDiaryById(diaryId)
            if (diary != null) {
                val updatedWater = diary.totalWaterMl + waterMl
                val updatedDiary = diary.copy(totalWaterMl = updatedWater)
                
                dailyDiaryDao.updateDailyDiary(updatedDiary)
                Log.d(TAG, "Updated water for diary ID: $diaryId, new total: $updatedWater ml")
                
                // Update in Firestore
                diaryFireStoreDataSource.updateDailyDiary(updatedDiary)
            }
        }
    }

    suspend fun updateDailyDiary(dailyDiary: DailyDiary) {
        withContext(Dispatchers.IO) {
            dailyDiaryDao.updateDailyDiary(dailyDiary)
            Log.d(TAG, "Updated daily diary with ID: ${dailyDiary.id}")

            // Update in Firestore
            diaryFireStoreDataSource.updateDailyDiary(dailyDiary)
        }
    }
}