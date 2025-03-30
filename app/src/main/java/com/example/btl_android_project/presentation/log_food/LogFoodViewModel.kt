package com.example.btl_android_project.presentation.log_food

import androidx.lifecycle.ViewModel
import com.example.btl_android_project.local.entity.Food
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogFoodViewModel @Inject constructor(): ViewModel() {
    val foods = listOf(
        Food(id = 1, name = "Food 1", calories = 100f),
    )
}