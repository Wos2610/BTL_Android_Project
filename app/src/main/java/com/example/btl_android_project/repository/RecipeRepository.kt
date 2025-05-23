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
    private val mealRepository: MealRepository,
    private val dailyDiaryRepository: DailyDiaryRepository
) {
    suspend fun getRecipesByUserId(userId: String): Flow<List<Recipe>>{
        return withContext(Dispatchers.IO) {
            recipeDao.getRecipesByUserId(userId)
        }
    }

    suspend fun getRecipeById(id: String): Recipe? {
        return withContext(Dispatchers.IO) {
            recipeDao.getRecipeById(id)
        }
    }

    suspend fun insertRecipe(recipe: Recipe){
        withContext(Dispatchers.IO) {
            val recipeId = recipeFireStoreDataSource.addRecipe(recipe)
            val updatedRecipe = recipe.copy(id = recipeId)
            recipeDao.insertRecipe(updatedRecipe)
        }
    }

    suspend fun deleteRecipe(recipeId: String){
        withContext(Dispatchers.IO) {
            recipeFireStoreDataSource.deleteRecipe(recipeId)
            recipeDao.deleteRecipeById(recipeId)
        }
    }

    suspend fun pullFromFireStore(userId: String) {
        withContext(Dispatchers.IO) {
            val recipes = recipeFireStoreDataSource.getAllRecipesByUser(userId)
            Log.d("RecipeRepository", "Pulled ${recipes.size} recipes from Firestore")
            recipes.forEach { recipe ->
                Log.d("RecipeRepository", "Recipe: ${recipe.id}")
            }
            recipeDao.insertRecipes(recipes)
        }
    }

    suspend fun searchRecipes(query: String, userId: String): List<Recipe> {
        return withContext(Dispatchers.IO) {
            recipeDao.searchRecipes(query, userId)
        }
    }

    suspend fun updateRecipe(recipe: Recipe, userId: String) {
        withContext(Dispatchers.IO) {
            recipeFireStoreDataSource.updateRecipe(recipe)
            recipeDao.updateRecipe(recipe)
            mealRepository.calculateWhenRecipeChange(recipe.id)
            dailyDiaryRepository.recalculateWhenChanging(userId = userId)
        }
    }
}
