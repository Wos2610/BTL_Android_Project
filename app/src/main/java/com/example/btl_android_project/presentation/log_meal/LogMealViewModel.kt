package com.example.btl_android_project.presentation.log_meal

import androidx.lifecycle.ViewModel
import com.example.btl_android_project.local.entity.Meal

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogMealViewModel @Inject constructor(): ViewModel() {
    val meals = listOf(
        Meal(id = 1, userId = 1, name = "Breakfast", totalCalories = 500f, mealType = "Breakfast"),
    )
}