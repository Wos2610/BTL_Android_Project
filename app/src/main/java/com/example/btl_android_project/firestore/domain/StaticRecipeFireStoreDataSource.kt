package com.example.btl_android_project.firestore.domain

import com.example.btl_android_project.remote.model.StaticRecipe

interface StaticRecipeFireStoreDataSource {
    suspend fun addAllRecipes(recipes: List<StaticRecipe>)
}