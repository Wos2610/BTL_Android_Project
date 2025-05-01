package com.example.btl_android_project.repository

import android.util.Log
import com.example.btl_android_project.firestore.datasource.RecipeFireStoreDataSourceImpl
import com.example.btl_android_project.local.dao.RecipeDao
import com.example.btl_android_project.local.entity.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecipeRepository @Inject constructor(
    private val recipeDao: RecipeDao,
    private val recipeFireStoreDataSource: RecipeFireStoreDataSourceImpl,
) {

    fun getAllRecipes(): Flow<List<Recipe>> = recipeDao.getAllRecipes()

    suspend fun getRecipesByUserId(userId: Int): Flow<List<Recipe>>{
        return withContext(Dispatchers.IO) {
            recipeDao.getRecipesByUserId(userId)
        }
    }

    suspend fun getRecipeById(id: Int): Recipe? {
        return withContext(Dispatchers.IO) {
            recipeDao.getRecipeById(id)
        }
    }

    suspend fun insertOrUpdateRecipe(recipe: Recipe){
        withContext(Dispatchers.IO) {
            val recipeId = recipeDao.insertRecipe(recipe)
            val updatedRecipe = recipe.copy(id = recipeId.toInt())
            recipeFireStoreDataSource.addRecipe(updatedRecipe)
        }
    }

    suspend fun insertRecipes(recipes: List<Recipe>) = recipeDao.insertRecipes(recipes)

    suspend fun updateRecipe(recipe: Recipe) = recipeDao.updateRecipe(recipe)

    suspend fun deleteRecipe(recipeId: Int){
        withContext(Dispatchers.IO) {
            recipeFireStoreDataSource.deleteRecipe(recipeId)
            recipeDao.deleteRecipeById(recipeId)
        }
    }

    suspend fun deleteAllRecipes() = recipeDao.deleteAllRecipes()

    suspend fun pullFromFireStore(userId: Int = 0) {
        withContext(Dispatchers.IO) {
            val recipes = recipeFireStoreDataSource.getAllRecipesByUser(userId)
            Log.d("RecipeRepository", "Pulled ${recipes.size} recipes from Firestore")
            recipes.forEach { recipe ->
                Log.d("RecipeRepository", "Recipe: ${recipe.id}")
            }
            recipeDao.insertRecipes(recipes)
        }
    }

    suspend fun searchRecipes(query: String, userId: Int): List<Recipe> {
        return withContext(Dispatchers.IO) {
            recipeDao.searchRecipes(query, userId)
        }
    }

}
