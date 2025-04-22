package com.example.btl_android_project.firestore.domain

import com.example.btl_android_project.local.entity.Recipe
import com.example.btl_android_project.local.entity.MealRecipeCrossRef

interface MealRecipeCrossRefFireStoreDataSource {
    suspend fun insertMealRecipeCrossRef(recipe: MealRecipeCrossRef)
    suspend fun updateMealRecipeCrossRef(recipe: MealRecipeCrossRef)
    suspend fun deleteMealRecipeCrossRef(recipeId: String)
    suspend fun getMealRecipeCrossRefById(recipeId: String): MealRecipeCrossRef?
    suspend fun getAllMealRecipeCrossRefsByUser(userId: Int): List<MealRecipeCrossRef>
    suspend fun searchMealRecipeCrossRefs(query: String, userId: Int): List<MealRecipeCrossRef>
}