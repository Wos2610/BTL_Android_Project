package com.example.btl_android_project.firestore.domain

import com.example.btl_android_project.local.entity.Recipe

interface RecipeFireStoreDataSource {
    fun addAllRecipes(recipes: List<Recipe>)
    fun addRecipe(recipe: Recipe)
    fun deleteRecipe(recipeId: Int)
}