package com.example.btl_android_project.entity

import java.io.Serializable
import java.util.Date

data class DailyDiary(
    val id: Int = 0,
    val userId: Int = 0,
    val logDate: Date = Date(),
    val caloriesRemaining: Float = 0f,
    val totalFoodCalories: Float = 0f,
    val totalExerciseCalories: Float = 0f,
    val totalWaterMl: Int = 0
) : Serializable