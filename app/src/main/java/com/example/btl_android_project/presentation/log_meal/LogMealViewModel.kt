package com.example.btl_android_project.presentation.log_meal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.local.entity.Meal
import com.example.btl_android_project.repository.MealFoodCrossRefRepository
import com.example.btl_android_project.repository.MealRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogMealViewModel @Inject constructor(
    val mealRepository: MealRepository,
    val mealFoodCrossRefRepository: MealFoodCrossRefRepository
): ViewModel() {
    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals = _meals.asStateFlow()

    fun loadMealFoodCrossRef(userId: Int) {
        viewModelScope.launch {
            mealFoodCrossRefRepository.pullFromFireStore(userId)
        }
    }

    fun loadMeals(userId: Int) {
        viewModelScope.launch {
            val allMeals = mealRepository.getMealsByUserId(userId)
            _meals.value = allMeals
        }
    }

    fun searchMeals(query: String) {
        viewModelScope.launch {
            val meals = mealRepository.searchMeals(query)
            _meals.value = meals
        }
    }
}