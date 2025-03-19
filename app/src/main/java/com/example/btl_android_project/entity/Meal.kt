package com.example.btl_android_project.entity

import java.io.Serializable

data class Meal(
    val id: Int = 0,
    val userId: Int = 0,
    val name: String = "",
    val mealType: String = "",
    val totalCalories: Float = 0f
) : Serializable