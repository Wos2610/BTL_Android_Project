package com.example.btl_android_project.firestore.domain

import com.example.btl_android_project.local.entity.Food

interface FoodFireStoreDataSource {
    suspend fun addFood(food: Food): String
    suspend fun updateFood(food: Food)
    suspend fun deleteFood(foodId: String)
    suspend fun getFoodById(foodId: String): Food?
    suspend fun getAllFoodsByUser(userId: Int): List<Food>
    suspend fun searchFoods(query: String, userId: Int): List<Food>
}