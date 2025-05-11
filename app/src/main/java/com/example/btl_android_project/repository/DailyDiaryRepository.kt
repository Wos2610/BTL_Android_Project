package com.example.btl_android_project.repository

import android.util.Log
import com.example.btl_android_project.firestore.datasource.DailyDiaryFireStoreDataSourceImpl
import com.example.btl_android_project.local.dao.DailyDiaryDao
import com.example.btl_android_project.local.entity.DailyDiary
import com.example.btl_android_project.local.entity.DiaryWithAllNutrition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class DailyDiaryRepository @Inject constructor(
    private val dailyDiaryDao: DailyDiaryDao,
    private val diaryFireStoreDataSource: DailyDiaryFireStoreDataSourceImpl,
    private val diaryFoodCrossRefRepository: DiaryFoodCrossRefRepository,
    private val diaryRecipeCrossRefRepository: DiaryRecipeCrossRefRepository,
    private val diaryMealCrossRefRepository: DiaryMealCrossRefRepository,
    private val userProfileRepository: UserProfileRepository,
) {
    private val TAG = "DailyDiaryRepository"

    suspend fun getDailyDiaryByDate(userId: String, date: LocalDate): DailyDiary? {
        return withContext(Dispatchers.IO) {
            dailyDiaryDao.getDailyDiaryByDate(userId, date)
        }
    }

    suspend fun getDiaryByDate(userId: String, date: LocalDate): DiaryWithAllNutrition? {
        return withContext(Dispatchers.IO) {
            dailyDiaryDao.getDiaryByDate(userId, date)
        }
    }

    suspend fun getOrCreateDailyDiary(userId: String, date: LocalDate): DailyDiary {
        return withContext(Dispatchers.IO) {
            val existingDiary = dailyDiaryDao.getDailyDiaryByDate(userId, date)

            if (existingDiary != null) {
                existingDiary
            } else {
                Log.d(TAG, "Creating new daily diary for user ID: $userId on date: $date")
                val userProfile = userProfileRepository.getUserProfileByUserId(userId)
                val caloriesGoal = userProfile?.calorieGoal?.toFloat() ?: 0f
                val newDiary = DailyDiary(
                    userId = userId,
                    logDate = date,
                    caloriesRemaining = 0f,
                    totalFoodCalories = 0f,
                    totalExerciseCalories = 0f,
                    totalWaterMl = 0,
                    totalFat = 0f,
                    totalCarbs = 0f,
                    totalProtein = 0f,
                    caloriesGoal = caloriesGoal
                )

                val diaryId = diaryFireStoreDataSource.insertDailyDiary(newDiary)
                val completeDiary = newDiary.copy(id = diaryId)
                dailyDiaryDao.insertDailyDiary(completeDiary)

                completeDiary
            }
        }
    }


    suspend fun addWater(diaryId: String, waterMl: Int) {
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

            diaryFireStoreDataSource.updateDailyDiary(dailyDiary)
        }
    }

    suspend fun pullFromFireStoreByUserId(userId: String) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Pulling daily diaries from Firestore for user ID: $userId")
            val diaries = diaryFireStoreDataSource.getDailyDiariesByUserId(userId)
            Log.d(TAG, "Pulled ${diaries.size} daily diaries from Firestore for user ID: $userId")
            dailyDiaryDao.deleteAllDailyDiaries()
            dailyDiaryDao.insertAllDailyDiaries(diaries)

            diaries.forEach { diary ->
                val diaryId = diary.id
                Log.d(TAG, "Processing daily diary with ID: $diaryId")

                diaryFoodCrossRefRepository.pullFromFireStore(diaryId)
                diaryRecipeCrossRefRepository.pullFromFireStore(diaryId)
                diaryMealCrossRefRepository.pullFromFireStore(diaryId)
            }
        }
    }

    suspend fun recalculateWhenChanging(userId: String) {
        withContext(Dispatchers.IO){
            val today = LocalDate.now()
            val existingDiary = dailyDiaryDao.getDailyDiaryByDate(userId, today)

            if (existingDiary == null) {
                Log.d(TAG, "No diary found for user ID: $userId on date: $today")
                return@withContext
            }

            Log.d(TAG, "Recalculating diary for user ID: $userId on date: $today")

            val diaryWithAllNutrition = dailyDiaryDao.getDiaryByDate(userId, today)

            val diary = diaryWithAllNutrition?.diary
            val foods = diaryWithAllNutrition?.foods ?: emptyList()
            val recipes = diaryWithAllNutrition?.recipes ?: emptyList()
            val meals = diaryWithAllNutrition?.meals ?: emptyList()

            val totalCalories = foods.sumOf { it.calories.toDouble() } + recipes.sumOf { it.calories } + meals.sumOf { it.totalCalories.toDouble() }
            val totalFat = foods.sumOf { it.fat.toDouble() } + recipes.sumOf { it.fat } + meals.sumOf { it.totalFat.toDouble() }
            val totalCarbs = foods.sumOf { it.carbs.toDouble() } + recipes.sumOf { it.carbs } + meals.sumOf { it.totalCarbs.toDouble() }
            val totalProtein = foods.sumOf { it.protein.toDouble() } + recipes.sumOf { it.protein } + meals.sumOf { it.totalProtein.toDouble() }


            val updatedDiary = diary?.copy(
                totalFoodCalories = totalCalories.toFloat(),
                totalFat = totalFat.toFloat(),
                totalCarbs = totalCarbs.toFloat(),
                totalProtein = totalProtein.toFloat()
            )

            updatedDiary?.let {
                dailyDiaryDao.updateDailyDiary(it)
                diaryFireStoreDataSource.updateDailyDiary(it)
            }
        }

    }

    suspend fun getDailyDiaryById(id: String): DailyDiary? {
        return withContext(Dispatchers.IO) {
            dailyDiaryDao.getDailyDiaryById(id)
        }
    }
}