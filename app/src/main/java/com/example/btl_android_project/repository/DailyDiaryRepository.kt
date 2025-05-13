package com.example.btl_android_project.repository

import android.util.Log
import com.example.btl_android_project.firestore.datasource.DailyDiaryFireStoreDataSourceImpl
import com.example.btl_android_project.local.dao.DailyDiaryDao
import com.example.btl_android_project.local.entity.DailyDiary
import com.example.btl_android_project.local.entity.DiaryWithAllNutrition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
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
    private val diaryExerciseCrossRefRepository: DiaryExerciseCrossRefRepository,
    private val waterCrossRefRepository: LogWaterRepository
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
            try {
                Log.d(TAG, "Pulling daily diaries from Firestore by user ID: $userId")
                val startTime = System.currentTimeMillis()

                val diaries = diaryFireStoreDataSource.getDailyDiariesByUserId(userId)
                Log.d(TAG, "Pulled ${diaries.size} daily diaries from Firestore in ${System.currentTimeMillis() - startTime}ms")

                if (diaries.isEmpty()) {
                    Log.d(TAG, "No daily diaries found for user $userId")
                    return@withContext
                }

                dailyDiaryDao.insertAllDailyDiaries(diaries)

                val diaryIds = diaries.map { it.id }

                val pullCrossRefsTime = System.currentTimeMillis()
                coroutineScope {
                    val foodCrossRefJob = launch {
                        try {
                            val foodStartTime = System.currentTimeMillis()
                            diaryFoodCrossRefRepository.pullFromFireStoreByDiaryIds(diaryIds)
                            Log.d(TAG, "Pulled food cross refs for ${diaryIds.size} diaries in ${System.currentTimeMillis() - foodStartTime}ms")
                        } catch (e: Exception) {
                            Log.e(TAG, "Error pulling food cross refs", e)
                        }
                    }

                    val recipeCrossRefJob = launch {
                        try {
                            val recipeStartTime = System.currentTimeMillis()
                            diaryRecipeCrossRefRepository.pullFromFireStoreByDiaryIds(diaryIds)
                            Log.d(TAG, "Pulled recipe cross refs for ${diaryIds.size} diaries in ${System.currentTimeMillis() - recipeStartTime}ms")
                        } catch (e: Exception) {
                            Log.e(TAG, "Error pulling recipe cross refs", e)
                        }
                    }

                    val mealCrossRefJob = launch {
                        try {
                            val mealStartTime = System.currentTimeMillis()
                            diaryMealCrossRefRepository.pullFromFireStoreByDiaryIds(diaryIds)
                            Log.d(TAG, "Pulled meal cross refs for ${diaryIds.size} diaries in ${System.currentTimeMillis() - mealStartTime}ms")
                        } catch (e: Exception) {
                            Log.e(TAG, "Error pulling meal cross refs", e)
                        }
                    }

//                    val waterCrossRefJob = launch {
//                        try {
//                            val waterStartTime = System.currentTimeMillis()
//                            waterCrossRefRepository.pullFromFireStoreByDiaryIds(diaryIds)
//                            Log.d(TAG, "Pulled water cross refs for ${diaryIds.size} diaries in ${System.currentTimeMillis() - waterStartTime}ms")
//                        } catch (e: Exception) {
//                            Log.e(TAG, "Error pulling water cross refs", e)
//                        }
//                    }

                    val exerciseCrossRefJob = launch {
                        try {
                            val exerciseStartTime = System.currentTimeMillis()
                            diaryExerciseCrossRefRepository.pullFromFireStoreByDiaryIds(diaryIds)
                            Log.d(TAG, "Pulled exercise cross refs for ${diaryIds.size} diaries in ${System.currentTimeMillis() - exerciseStartTime}ms")
                        } catch (e: Exception) {
                            Log.e(TAG, "Error pulling exercise cross refs", e)
                        }
                    }

                    foodCrossRefJob.join()
                    recipeCrossRefJob.join()
                    mealCrossRefJob.join()
//                    waterCrossRefJob.join()
                    exerciseCrossRefJob.join()
                }

                Log.d(TAG, "Completed pulling cross refs in ${System.currentTimeMillis() - pullCrossRefsTime}ms")
                Log.d(TAG, "Total daily diary sync completed in ${System.currentTimeMillis() - startTime}ms")

            } catch (e: Exception) {
                Log.e(TAG, "Error pulling daily diaries from Firestore", e)
                throw e
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

            if (diaryWithAllNutrition == null) {
                Log.d(TAG, "No diary found for user ID: $userId on date: $today")
                return@withContext
            }

            val diary = diaryWithAllNutrition.diary

            val diaryFoodCrossRefs = diaryFoodCrossRefRepository.getDiaryFoodCrossRefsByDiaryId(diary.id)
            val diaryMealCrossRefs = diaryMealCrossRefRepository.getDiaryMealCrossRefsByDiaryId(diary.id)
            val diaryRecipeCrossRefs = diaryRecipeCrossRefRepository.getDiaryRecipeCrossRefsByDiaryId(diary.id)
            val diaryExerciseCrossRefs = diaryExerciseCrossRefRepository.getDiaryExerciseCrossRefsByDiaryId(diary.id)

            var totalCalories = 0.0
            var totalFat = 0.0
            var totalCarbs = 0.0
            var totalProtein = 0.0
            var totalExerciseCalories = 0.0

            diaryFoodCrossRefs?.forEach { crossRef ->
                val food = diaryWithAllNutrition.foods.find { it.id == crossRef.foodId }
                food?.let {
                    totalCalories += it.calories * crossRef.servings
                    totalFat += it.fat * crossRef.servings
                    totalCarbs += it.carbs * crossRef.servings
                    totalProtein += it.protein * crossRef.servings
                }
            }

            diaryMealCrossRefs?.forEach { crossRef ->
                val meal = diaryWithAllNutrition.meals.find { it.id == crossRef.mealId }
                meal?.let {
                    totalCalories += it.totalCalories * crossRef.servings
                    totalFat += it.totalFat * crossRef.servings
                    totalCarbs += it.totalCarbs * crossRef.servings
                    totalProtein += it.totalProtein * crossRef.servings
                }

            }

            diaryRecipeCrossRefs?.forEach { crossRef ->
                val recipe = diaryWithAllNutrition.recipes.find { it.id == crossRef.recipeId }
                recipe?.let {
                    totalCalories += it.calories * crossRef.servings
                    totalFat += it.fat * crossRef.servings
                    totalCarbs += it.carbs * crossRef.servings
                    totalProtein += it.protein * crossRef.servings
                }
            }

            diaryExerciseCrossRefs?.forEach { crossRef ->
                val exercise = diaryWithAllNutrition.exercises.find { it.id == crossRef.exerciseId }
                exercise?.let {
                    totalExerciseCalories += it.caloriesBurned * crossRef.servings
                }
            }

            val updatedDiary = diary.copy(
                totalFoodCalories = totalCalories.toFloat(),
                totalFat = totalFat.toFloat(),
                totalCarbs = totalCarbs.toFloat(),
                totalProtein = totalProtein.toFloat(),
                caloriesRemaining = diary.caloriesGoal - totalCalories.toFloat() + totalExerciseCalories.toFloat(),
                totalExerciseCalories = totalExerciseCalories.toFloat(),
            )

            updatedDiary.let {
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