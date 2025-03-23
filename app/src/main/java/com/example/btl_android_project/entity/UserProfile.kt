package com.example.btl_android_project.entity

import java.io.Serializable

data class UserProfile(
    val userId: Int = 0,
    val height: Float = 0f,
    val currentWeight: Float = 0f,
    val initialWeight: Float = 0f,
    val weeklyGoal: String = "",
    val weightGoal: Float = 0f,
    val waterGoal: Int = 0,
    val calorieGoal: Int = 0
) : Serializable