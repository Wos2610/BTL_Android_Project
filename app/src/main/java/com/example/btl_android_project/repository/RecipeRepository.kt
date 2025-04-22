package com.example.btl_android_project.repository

import android.util.Log
import com.example.btl_android_project.firestore.domain.RecipeFireStoreDataSource
import com.example.btl_android_project.local.dao.RecipeDao
import com.example.btl_android_project.local.entity.Recipe
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecipeRepository @Inject constructor(
    private val recipeDao: RecipeDao,
    private val recipeFireStoreDataSource: RecipeFireStoreDataSource,
) {

    fun getAllRecipes(): Flow<List<Recipe>> = recipeDao.getAllRecipes()

    fun getRecipesByUserId(userId: Int): Flow<List<Recipe>> = recipeDao.getRecipesByUserId(userId)

    suspend fun getRecipeById(id: Int): Recipe? = recipeDao.getRecipeById(id)

    suspend fun insertOrUpdateRecipe(recipe: Recipe){
        val newRecipe = recipe.copy(
            calories = recipe.ingredients.sumOf { it.foodNutrients.firstOrNull{ it.name.contains("Energy") }?.amount?.toInt() ?: 0 },
        )
        val recipeId = recipeDao.insertRecipe(newRecipe)
        val updatedRecipe = newRecipe.copy(id = recipeId.toInt())
        recipeFireStoreDataSource.addRecipe(updatedRecipe)
    }

    suspend fun insertRecipes(recipes: List<Recipe>) = recipeDao.insertRecipes(recipes)

    suspend fun updateRecipe(recipe: Recipe) = recipeDao.updateRecipe(recipe)

    suspend fun deleteRecipe(recipeId: Int){
//        recipeFireStoreDataSource.deleteRecipe(recipe.id)
        recipeDao.deleteRecipeById(recipeId)
    }

    suspend fun deleteAllRecipes() = recipeDao.deleteAllRecipes()

    suspend fun pullFromFireStore(userId: Int = 0) {
        val recipes = recipeFireStoreDataSource.getAllRecipesByUser(userId)
        Log.d("RecipeRepository", "Pulled ${recipes.size} recipes from Firestore")
        recipes.forEach { recipe ->
            Log.d("RecipeRepository", "Recipe: ${recipe.id}")
        }
        recipeDao.insertRecipes(recipes)
    }

}
