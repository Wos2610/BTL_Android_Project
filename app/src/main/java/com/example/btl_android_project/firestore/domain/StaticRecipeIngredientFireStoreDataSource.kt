package com.example.btl_android_project.firestore.domain

import com.example.btl_android_project.local.entity.StaticRecipeIngredient
import com.example.btl_android_project.remote.model.StaticFood

interface StaticRecipeIngredientFireStoreDataSource {
    fun addAllRecipeIngredients(recipeIngredients: List<StaticRecipeIngredient>)
    suspend fun pullRecipeIngredients(): List<StaticRecipeIngredient>
}