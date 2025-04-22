package com.example.btl_android_project.repository

import com.example.btl_android_project.local.dao.FoodDao
import com.example.btl_android_project.local.dao.MealDao
import com.example.btl_android_project.local.dao.RecipeDao
import com.example.btl_android_project.local.entity.Meal
import com.example.btl_android_project.local.entity.MealFoodCrossRef
import com.example.btl_android_project.local.entity.MealRecipeCrossRef
import com.example.btl_android_project.local.entity.MealWithFoodsAndRecipes
import com.example.btl_android_project.presentation.log_meal.MealItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import android.util.Log
import com.example.btl_android_project.firestore.domain.MealFireStoreDataSource

class MealRepository @Inject constructor(
    private val mealDao: MealDao,
    private val foodDao: FoodDao,
    private val recipeDao: RecipeDao,
    private val mealFoodCrossRefRepository: MealFoodCrossRefRepository,
    private val mealRecipeCrossRefRepository: MealRecipeCrossRefRepository,
    private val mealFireStoreDataSource: MealFireStoreDataSource
) {

    suspend fun createMeal(
        name: String,
        mealType: String,
        userId: Int,
        selectedFoodItems: List<MealItem.FoodItem>,
        selectedRecipeItems: List<MealItem.RecipeItem>
    ) {
        withContext(Dispatchers.IO) {
            val meal = Meal(
                name = name,
                mealType = mealType,
                userId = userId,
                totalCalories = 0f
            )
            val mealId = mealDao.insertMeal(meal).toInt()
            Log.d("MealRepository", "Inserted meal with ID: $mealId")

            selectedFoodItems.forEach { food ->
                mealFoodCrossRefRepository.insertMealFoodCrossRef(MealFoodCrossRef(mealId, food.food.id, food.food.servings, meal.userId))
                Log.d("MealRepository", "Inserted MealFoodCrossRef: mealId=$mealId, foodId=${food.food.id}, servings=${food.food.servings}")
            }

            selectedRecipeItems.forEach { recipe ->
                mealRecipeCrossRefRepository.insertMealRecipeCrossRef(MealRecipeCrossRef(mealId, recipe.recipe.id, recipe.recipe.servings, meal.userId))
            }

            val totalFoodCalories = selectedFoodItems.sumOf { it.food.calories.toDouble() }
            val totalRecipeCalories = selectedRecipeItems.sumOf { it.recipe.calories.toDouble() }

            val totalCalories = (totalFoodCalories + totalRecipeCalories).toFloat()

            val updatedMeal = Meal(
                id = mealId,
                name = name,
                mealType = mealType,
                userId = userId,
                totalCalories = totalCalories
            )
            mealDao.updateMeal(updatedMeal)
            mealFireStoreDataSource.addMeal(updatedMeal)
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

    suspend fun pullFromFireStore(userId: Int) {
        Log.d("MealRepository", "Pulling meals from Firestore")
        val meals = mealFireStoreDataSource.getAllMealsByUser(userId)
        Log.d("MealRepository", "Pulled ${meals.size} meals from Firestore")
        mealDao.insertAllMeals(meals)
    }
}
