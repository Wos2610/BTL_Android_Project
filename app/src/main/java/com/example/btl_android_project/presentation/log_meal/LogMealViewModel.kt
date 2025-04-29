package com.example.btl_android_project.presentation.log_meal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.local.entity.Meal
import com.example.btl_android_project.repository.MealFoodCrossRefRepository
import com.example.btl_android_project.repository.MealRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogMealViewModel @Inject constructor(
    val mealRepository: MealRepository,
    val mealFoodCrossRefRepository: MealFoodCrossRefRepository
): ViewModel() {
    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals = _meals.asStateFlow()
    var userId: Int = 0

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
}