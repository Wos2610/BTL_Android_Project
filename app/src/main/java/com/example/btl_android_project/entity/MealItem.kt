package com.example.btl_android_project.entity

import java.io.Serializable

data class MealItem(
    val mealId: Int = 0,
    val foodId: Int? = null, // Nullable if using recipes instead
    val recipeId: Int? = null,
    val quantity: Float = 0f
) : Serializable