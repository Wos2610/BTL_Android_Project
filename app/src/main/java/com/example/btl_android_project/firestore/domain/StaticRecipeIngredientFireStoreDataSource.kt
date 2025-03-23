package com.example.btl_android_project.firestore.domain

import com.example.btl_android_project.entity.StaticRecipeIngredient

interface StaticRecipeIngredientFireStoreDataSource {
    fun addAllRecipeIngredients(recipeIngredients: List<StaticRecipeIngredient>)
}