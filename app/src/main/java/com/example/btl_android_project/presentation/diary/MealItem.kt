package com.example.btl_android_project.presentation.diary

import com.example.btl_android_project.local.enums.MealType

data class MealItem(
    val name: String,
    var serving: String,
    var calories: Int,
    val id: String,
    val type: Type,
    val mealType: MealType,
    var servings: Int,
)

enum class Type {
    FOOD,
    RECIPE,
    MEAL,
    WATER,
    EXERCISE,
}