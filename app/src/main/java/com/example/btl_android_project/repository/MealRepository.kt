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
    private val mealFireStoreDataSource: MealFireStoreDataSourceImpl
) {

    suspend fun createMeal(
        name: String,
        mealType: String,
        userId: Int,
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
                totalCalories = 0f
            )
            val mealId = mealDao.insertMeal(meal).toInt()
            Log.d("MealRepository", "Inserted meal with ID: $mealId")

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
            }

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

    suspend fun getMealWithFoodsAndRecipes(mealId: Int): MealWithFoodsAndRecipes {
        return withContext(Dispatchers.IO) {
            val meal = mealDao.getMealById(mealId)
            val mealFoodCrossRefs = mealFoodCrossRefRepository.getMealFoodCrossRefById(mealId)
            val mealRecipeCrossRefs = mealRecipeCrossRefRepository.getMealRecipeCrossRefById(mealId)

            // Use async to parallelize the food fetching
            val foods = mealFoodCrossRefs?.map { crossRef ->
                async {
                    foodDao.getFoodById(crossRef.foodId)?.copy(servings = crossRef.servings)
                }
            }?.awaitAll()

            // Use async to parallelize the recipe fetching
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

    suspend fun editMeal(
        mealId: Int,
        name: String,
        mealType: String,
        userId: Int,
        selectedFoodItems: List<MealItem.FoodItem>,
        selectedRecipeItems: List<MealItem.RecipeItem>,
        totalCalories: Int,
        totalCarbs: Int,
        totalProtein: Int,
        totalFat: Int
    ) {
        withContext(Dispatchers.IO) {
            // Cập nhật thông tin bữa ăn
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

            // Cập nhật bữa ăn trong cơ sở dữ liệu cục bộ
            mealDao.updateMeal(updatedMeal)

            // Xóa tất cả các mối quan hệ tham chiếu hiện có
            mealFoodCrossRefRepository.deleteMealFoodCrossRefByMealId(mealId)
            mealRecipeCrossRefRepository.deleteMealRecipeCrossRefByMealId(mealId)

            Log.d("MealRepository", "Updating meal with ID: $mealId")

            // Thêm lại các mối quan hệ thực phẩm mới
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

            // Thêm lại các mối quan hệ công thức nấu ăn mới
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

            // Cập nhật dữ liệu trên Firestore
            mealFireStoreDataSource.updateMeal(updatedMeal)
        }
    }

    suspend fun searchMeals(query: String, userId: Int): List<Meal> {
        return mealDao.searchMeals(query, userId)
    }

    suspend fun getMealsByUserId(userId: Int): Flow<List<Meal>> {
        return withContext(Dispatchers.IO) {
            mealDao.getMealsByUserId(userId)
        }
    }

    suspend fun getMealById(mealId: Int): Meal? {
        return withContext(Dispatchers.IO) {
            mealDao.getMealById(mealId)
        }
    }

    suspend fun pullFromFireStoreByUserId(userId: Int) {
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
}
