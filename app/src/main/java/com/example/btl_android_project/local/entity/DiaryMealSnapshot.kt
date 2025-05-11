package com.example.btl_android_project.local.entity

import com.example.btl_android_project.local.enums.MealType
import java.io.Serializable

data class DiaryMealSnapshot(
    val mealId: String,
    val mealName: String,
    val servings: Int,
    val mealType: MealType,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val foods: List<DairyFoodSnapshot> = emptyList(),
    val recipes: List<DiaryRecipeSnapshot> = emptyList(),
) : Serializable