package com.example.btl_android_project.presentation.diary

import com.example.btl_android_project.local.enums.MealType

data class MealItem(
    val name: String,
    val serving: String,
    val calories: Int,
    val id: String,
    val type: Type,
    val mealType: MealType
)

enum class Type {
    FOOD,
    RECIPE,
    MEAL,
}