package com.example.btl_android_project.firestore.domain

import com.example.btl_android_project.local.entity.Meal

interface MealFireStoreDataSource {
    fun addAllMeals(meals: List<Meal>)
    fun addMeal(meal: Meal)
    fun deleteMeal(mealId: Int)
    suspend fun getAllMealsByUser(userId: Int): List<Meal>
    suspend fun updateMeal(meal: Meal)
}