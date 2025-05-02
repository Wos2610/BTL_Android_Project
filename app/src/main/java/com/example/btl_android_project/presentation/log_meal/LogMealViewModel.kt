package com.example.btl_android_project.presentation.log_meal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.auth.FirebaseAuthDataSource
import com.example.btl_android_project.local.entity.DiaryMealCrossRef
import com.example.btl_android_project.local.entity.Meal
import com.example.btl_android_project.repository.DailyDiaryRepository
import com.example.btl_android_project.repository.DiaryMealCrossRefRepository
import com.example.btl_android_project.repository.MealFoodCrossRefRepository
import com.example.btl_android_project.repository.MealRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LogMealViewModel @Inject constructor(
    val mealRepository: MealRepository,
    val mealFoodCrossRefRepository: MealFoodCrossRefRepository,
    val dailyDiaryRepository: DailyDiaryRepository,
    val dailyDiaryMealCrossRefRepository: DiaryMealCrossRefRepository,
    val firebaseAuthDataSource: FirebaseAuthDataSource,
): ViewModel() {
    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals = _meals.asStateFlow()
    var userId: String = firebaseAuthDataSource.getCurrentUserId().toString()
    val logDate: LocalDate = LocalDate.now()

    fun loadMealFoodCrossRef() {
        viewModelScope.launch {
            mealFoodCrossRefRepository.pullFromFireStore(userId)
        }
    }

    fun loadMeals() {
        viewModelScope.launch {
            mealRepository.getMealsByUserId(userId).collectLatest {meals ->
                _meals.value = meals
            }
        }
    }

    fun searchMeals(query: String) {
        Log.d("LogMealViewModel", "Searching meals with query: $query")
        viewModelScope.launch {
            val meals = mealRepository.searchMeals(query, userId)
            _meals.value = meals
        }
    }

    fun addMealToDiary(
        mealId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val dailyDiary = dailyDiaryRepository.getOrCreateDailyDiary(userId, logDate)
            val dairyMealCrossRef = DiaryMealCrossRef(
                mealId = mealId,
                diaryId = dailyDiary.id,
                userId = userId,
                servings = 1,
                mealType = null,
            )
            dailyDiaryMealCrossRefRepository.insertDiaryMealCrossRef(dairyMealCrossRef)

            val meal = mealRepository.getMealById(mealId)
            val updatedDailyDiary = dailyDiary.copy(
                totalFoodCalories = dailyDiary.totalFoodCalories + (meal?.totalCalories ?: 0f),
                totalCarbs = dailyDiary.totalCarbs + (meal?.totalCarbs ?: 0f),
                totalProtein = dailyDiary.totalProtein + (meal?.totalProtein ?: 0f),
                totalFat = dailyDiary.totalFat + (meal?.totalFat ?: 0f),
            )

            dailyDiaryRepository.updateDailyDiary(updatedDailyDiary)
            onSuccess()
        }
    }
}