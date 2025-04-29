package com.example.btl_android_project.firestore.domain

import com.example.btl_android_project.local.entity.Food
import com.example.btl_android_project.local.entity.MealFoodCrossRef

interface MealFoodCrossRefFireStoreDataSource {
    suspend fun insertMealFoodCrossRef(food: MealFoodCrossRef)
    suspend fun updateMealFoodCrossRef(food: MealFoodCrossRef)
    suspend fun deleteMealFoodCrossRef(foodId: String)
    suspend fun getMealFoodCrossRefById(foodId: String): MealFoodCrossRef?
    suspend fun getAllMealFoodCrossRefsByUser(userId: Int): List<MealFoodCrossRef>
    suspend fun searchMealFoodCrossRefs(query: String, userId: Int): List<MealFoodCrossRef>
    suspend fun deleteMealFoodCrossRefByMealId(mealId: Int)
}