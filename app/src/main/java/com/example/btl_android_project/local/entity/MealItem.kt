package com.example.btl_android_project.local.entity

import java.io.Serializable

data class MealItem(
    val mealId: String = "",
    val foodId: String = "",
    val recipeId: String = "",
    val quantity: Float = 0f
) : Serializable