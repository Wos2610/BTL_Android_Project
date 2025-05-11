package com.example.btl_android_project.repository

import android.util.Log
import com.example.btl_android_project.firestore.datasource.MealFireStoreDataSourceImpl
import com.example.btl_android_project.local.dao.FoodDao
import com.example.btl_android_project.local.dao.MealDao
import com.example.btl_android_project.local.dao.RecipeDao
import com.example.btl_android_project.local.entity.Meal
import com.example.btl_android_project.local.entity.MealFoodCrossRef
import com.example.btl_android_project.local.entity.MealRecipeCrossRef
import com.example.btl_android_project.local.entity.MealWithFoodsAndRecipes
import com.example.btl_android_project.presentation.log_meal.MealItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MealRepository @Inject constructor(
    private val mealDao: MealDao,
    private val foodDao: FoodDao,
    private val recipeDao: RecipeDao,
    private val mealFoodCrossRefRepository: MealFoodCrossRefRepository,
    private val mealRecipeCrossRefRepository: MealRecipeCrossRefRepository,
    private val mealFireStoreDataSource: MealFireStoreDataSourceImpl,
    private val dailyDiaryRepository: DailyDiaryRepository
) {

    suspend fun createMeal(
        name: String,
        mealType: String,
        userId: String,
        selectedFoodItems: List<MealItem.FoodItem>,
        selectedRecipeItems: List<MealItem.RecipeItem>,
        totalCalories: Int,
        totalCarbs: Int,
        totalProtein: Int,
        totalFat: Int
    ) {
        withContext(Dispatchers.IO) {
            val meal = Meal(
                name = name,
                mealType = mealType,
                userId = userId,
                totalCalories = totalCalories.toFloat(),
                totalCarbs = totalCarbs.toFloat(),
                totalProtein = totalProtein.toFloat(),
                totalFat = totalFat.toFloat()
            )

            val mealId = mealFireStoreDataSource.addMeal(meal)
            Log.d("MealRepository", "Inserted meal with ID: $mealId")

            val updatedMeal = meal.copy(id = mealId)

            mealDao.insertMeal(updatedMeal)

            selectedFoodItems.forEach { food ->
                mealFoodCrossRefRepository.insertMealFoodCrossRef(
                    MealFoodCrossRef(
                        mealId,
                        food.food.id,
                        food.food.servings,
                        meal.userId
                    )
                )
                Log.d(
                    "MealRepository",
                    "Inserted MealFoodCrossRef: mealId=$mealId, foodId=${food.food.id}, servings=${food.food.servings}"
                )
            }

            selectedRecipeItems.forEach { recipe ->
                mealRecipeCrossRefRepository.insertMealRecipeCrossRef(
                    MealRecipeCrossRef(
                        mealId,
                        recipe.recipe.id,
                        recipe.recipe.servings,
                        meal.userId
                    )
                )
                Log.d(
                    "MealRepository",
                    "Inserted MealRecipeCrossRef: mealId=$mealId, recipeId=${recipe.recipe.id}, servings=${recipe.recipe.servings}"
                )
            }
        }
    }

    suspend fun getMealWithFoodsAndRecipes(mealId: String): MealWithFoodsAndRecipes {
        return withContext(Dispatchers.IO) {
            val meal = mealDao.getMealById(mealId) ?: throw IllegalArgumentException("Meal not found")
            val mealFoodCrossRefs = mealFoodCrossRefRepository.getMealFoodCrossRefById(mealId)
            val mealRecipeCrossRefs = mealRecipeCrossRefRepository.getMealRecipeCrossRefById(mealId)

            val foods = mealFoodCrossRefs?.map { crossRef ->
                async {
                    foodDao.getFoodById(crossRef.foodId)?.copy(servings = crossRef.servings)
                }
            }?.awaitAll()

            val recipes = mealRecipeCrossRefs?.map { crossRef ->
                async {
                    recipeDao.getRecipeById(crossRef.recipeId)?.copy(servings = crossRef.servings)
                }
            }?.awaitAll()

            MealWithFoodsAndRecipes(
                meal = meal,
                foods = foods.orEmpty().filterNotNull(),
                recipes = recipes.orEmpty().filterNotNull()
            )
        }
    }

    suspend fun initMeal(
        name: String,
        mealType: String,
        userId: Int
    ): Int {
        return withContext(Dispatchers.IO) {
            val meal = Meal(
                name = name,
                mealType = mealType,
                userId = userId.toString(),
                totalCalories = 0f
            )
            mealDao.insertMeal(meal).toInt()
        }
    }

    suspend fun editMeal(
        mealId: String,
        name: String,
        mealType: String,
        userId: String,
        selectedFoodItems: List<MealItem.FoodItem>,
        selectedRecipeItems: List<MealItem.RecipeItem>,
        totalCalories: Int,
        totalCarbs: Int,
        totalProtein: Int,
        totalFat: Int
    ) {
        withContext(Dispatchers.IO) {
            val updatedMeal = Meal(
                id = mealId,
                name = name,
                mealType = mealType,
                userId = userId,
                totalCalories = totalCalories.toFloat(),
                totalCarbs = totalCarbs.toFloat(),
                totalProtein = totalProtein.toFloat(),
                totalFat = totalFat.toFloat()
            )

            Log.d("MealRepository", "Updated meal: $updatedMeal")

            mealDao.updateMeal(updatedMeal)

            mealFoodCrossRefRepository.deleteMealFoodCrossRefByMealId(mealId)
            mealRecipeCrossRefRepository.deleteMealRecipeCrossRefByMealId(mealId)

            Log.d("MealRepository", "Updating meal with ID: $mealId")

            selectedFoodItems.forEach { food ->
                mealFoodCrossRefRepository.insertMealFoodCrossRef(
                    MealFoodCrossRef(
                        mealId,
                        food.food.id,
                        food.food.servings,
                        userId
                    )
                )
                Log.d(
                    "MealRepository",
                    "Updated MealFoodCrossRef: mealId=$mealId, foodId=${food.food.id}, servings=${food.food.servings}"
                )
            }

            selectedRecipeItems.forEach { recipe ->
                mealRecipeCrossRefRepository.insertMealRecipeCrossRef(
                    MealRecipeCrossRef(
                        mealId,
                        recipe.recipe.id,
                        recipe.recipe.servings,
                        userId
                    )
                )
                Log.d(
                    "MealRepository",
                    "Updated MealRecipeCrossRef: mealId=$mealId, recipeId=${recipe.recipe.id}, servings=${recipe.recipe.servings}"
                )
            }

            mealFireStoreDataSource.updateMeal(updatedMeal)
            dailyDiaryRepository.recalculateWhenChanging(userId = userId)
        }
    }

    suspend fun searchMeals(query: String, userId: String): List<Meal> {
        return mealDao.searchMeals(query, userId)
    }

    suspend fun getMealsByUserId(userId: String): Flow<List<Meal>> {
        return withContext(Dispatchers.IO) {
            mealDao.getMealsByUserId(userId)
        }
    }

    suspend fun getMealById(mealId: String): Meal? {
        return withContext(Dispatchers.IO) {
            mealDao.getMealById(mealId)
        }
    }

    suspend fun pullFromFireStoreByUserId(userId: String) {
        withContext(Dispatchers.IO) {
            Log.d("MealRepository", "Pulling meals from Firestore by user ID: $userId")
            val meals = mealFireStoreDataSource.getAllMealsByUser(userId)
            Log.d("MealRepository", "Pulled ${meals.size} meals from Firestore")
            mealDao.deleteAllMeals()
            mealDao.insertAllMeals(meals)

            meals.forEach { meal ->
                val mealId = meal.id
                Log.d("MealRepository", "Pulling food and recipe cross refs for meal ID: $mealId")
                mealFoodCrossRefRepository.pullFromFireStoreByMealId(mealId)
                mealRecipeCrossRefRepository.pullFromFireStoreByMealId(mealId)
            }
        }
    }

    suspend fun calculateWhenFoodChange(foodId: String) {
        withContext(Dispatchers.IO) {
            val mealFoodCrossRefs = mealFoodCrossRefRepository.getMealFoodCrossRefByFoodId(foodId)
            mealFoodCrossRefs?.forEach { crossRef ->
                val meal = mealDao.getMealById(crossRef.mealId)

                val mealWithFoodsAndRecipes = getMealWithFoodsAndRecipes(crossRef.mealId)

                val foods = mealWithFoodsAndRecipes.foods
                val recipes = mealWithFoodsAndRecipes.recipes

                val totalCalories = foods.sumOf { it.calories.toInt() * it.servings } + recipes.sumOf { it.calories * it.servings }
                val totalCarbs = foods.sumOf { it.carbs.toInt()  * it.servings } + recipes.sumOf { it.carbs * it.servings }
                val totalProtein = foods.sumOf { it.protein.toInt()  * it.servings } + recipes.sumOf { it.protein * it.servings }
                val totalFat = foods.sumOf { it.fat.toInt()  * it.servings } + recipes.sumOf { it.fat * it.servings }

                val updatedMeal = meal?.copy(
                    totalCalories = totalCalories.toFloat(),
                    totalCarbs = totalCarbs.toFloat(),
                    totalProtein = totalProtein.toFloat(),
                    totalFat = totalFat.toFloat()
                )
                updatedMeal?.let {
                    mealDao.updateMeal(it)
                    mealFireStoreDataSource.updateMeal(it)
                    Log.d("MealRepository", "Updated meal with ID: ${it.id}")
                }
            }
        }
    }

    suspend fun calculateWhenRecipeChange(recipeId: String) {
        withContext(Dispatchers.IO) {
            val mealRecipeCrossRefs =
                mealRecipeCrossRefRepository.getMealRecipeCrossRefByRecipeId(recipeId)
            mealRecipeCrossRefs?.forEach { crossRef ->
                val meal = mealDao.getMealById(crossRef.mealId)

                val mealWithFoodsAndRecipes = getMealWithFoodsAndRecipes(crossRef.mealId)

                val foods = mealWithFoodsAndRecipes.foods
                val recipes = mealWithFoodsAndRecipes.recipes

                val totalCalories =
                    foods.sumOf { it.calories.toInt() * it.servings } + recipes.sumOf { it.calories * it.servings }
                val totalCarbs =
                    foods.sumOf { it.carbs.toInt() * it.servings } + recipes.sumOf { it.carbs * it.servings }
                val totalProtein =
                    foods.sumOf { it.protein.toInt() * it.servings } + recipes.sumOf { it.protein * it.servings }
                val totalFat =
                    foods.sumOf { it.fat.toInt() * it.servings } + recipes.sumOf { it.fat * it.servings }

                val updatedMeal = meal?.copy(
                    totalCalories = totalCalories.toFloat(),
                    totalCarbs = totalCarbs.toFloat(),
                    totalProtein = totalProtein.toFloat(),
                    totalFat = totalFat.toFloat()
                )
                updatedMeal?.let {
                    mealDao.updateMeal(it)
                    mealFireStoreDataSource.updateMeal(it)
                    Log.d("MealRepository", "Updated meal with ID: ${it.id}")
                }
            }
        }
    }
}
