package com.example.btl_android_project.presentation.log_meal

import com.example.btl_android_project.local.entity.Food
import com.example.btl_android_project.local.entity.Recipe

sealed class MealItem {
    data class RecipeItem(val recipe: Recipe) : MealItem()
    data class FoodItem(val food: Food) : MealItem()
    data class Header(val title: String) : MealItem()
}