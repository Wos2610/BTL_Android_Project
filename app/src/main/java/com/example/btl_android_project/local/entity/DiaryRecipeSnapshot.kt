package com.example.btl_android_project.local.entity

import com.example.btl_android_project.local.enums.MealType
import java.io.Serializable

data class DiaryRecipeSnapshot(
    val recipeId: String,
    val recipeName: String,
    val servings: Int,
    val mealType: MealType,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val ingredients: List<RecipeIngredientSnapshot> = emptyList()
) : Serializable