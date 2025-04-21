package com.example.btl_android_project.presentation.log_meal

import androidx.lifecycle.ViewModel
import com.example.btl_android_project.local.entity.Meal
import com.example.btl_android_project.repository.MealRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogMealViewModel @Inject constructor(
    val mealRepository: MealRepository
): ViewModel() {
    private val _meals = mealRepository.getMeals()
    val meals = _meals
}