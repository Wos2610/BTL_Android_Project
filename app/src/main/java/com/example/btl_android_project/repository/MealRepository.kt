package com.example.btl_android_project.repository

import com.example.btl_android_project.local.dao.FoodDao
import com.example.btl_android_project.local.dao.MealDao
import com.example.btl_android_project.local.dao.RecipeDao
import com.example.btl_android_project.local.entity.Meal
import com.example.btl_android_project.local.entity.MealFoodCrossRef
import com.example.btl_android_project.local.entity.MealRecipeCrossRef
import com.example.btl_android_project.local.entity.MealWithFoodsAndRecipes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MealRepository @Inject constructor(
    private val mealDao: MealDao,
    private val foodDao: FoodDao,
    private val recipeDao: RecipeDao
) {

    suspend fun createMeal(
        name: String,
        mealType: String,
        userId: Int,
        selectedFoodIds: List<Int>,
        selectedRecipeIds: List<Int>
    ) {
        withContext(Dispatchers.IO) {
            val meal = Meal(
                name = name,
                mealType = mealType,
                userId = userId,
                totalCalories = 0f
            )
            val mealId = mealDao.insertMeal(meal).toInt()

            selectedFoodIds.forEach { foodId ->
                mealDao.insertMealFoodCrossRef(MealFoodCrossRef(mealId, foodId))
            }

            selectedRecipeIds.forEach { recipeId ->
                mealDao.insertMealRecipeCrossRef(MealRecipeCrossRef(mealId, recipeId))
            }

            val totalFoodCalories = foodDao.getFoodsByIds(selectedFoodIds).sumOf { it.calories.toDouble() }
            val totalRecipeCalories = recipeDao.getRecipesByIds(selectedRecipeIds).sumOf { it.calories.toDouble() }

            val totalCalories = (totalFoodCalories + totalRecipeCalories).toFloat()

            val updatedMeal = Meal(
                id = mealId,
                name = name,
                mealType = mealType,
                userId = userId,
                totalCalories = totalCalories
            )
            mealDao.updateMeal(updatedMeal)
        }
    }

    suspend fun getMealsOfUser(userId: Int): List<MealWithFoodsAndRecipes> {
        return mealDao.getMealsWithFoodsAndRecipesByUser(userId)
    }

    suspend fun deleteMeal(meal: Meal) {
        mealDao.deleteMeal(meal)
    }

    suspend fun getMeal(mealId: Int): MealWithFoodsAndRecipes {
        return mealDao.getMealWithFoodsAndRecipes(mealId)
    }

    suspend fun initMeal(
        name: String,
        mealType: String,
        userId: Int
    ) : Int {
        return withContext(Dispatchers.IO) {
            val meal = Meal(
                name = name,
                mealType = mealType,
                userId = userId,
                totalCalories = 0f
            )
            mealDao.insertMeal(meal).toInt()
        }
    }

    fun getMeals(): Flow<List<Meal>> = mealDao.getMeals()
}
