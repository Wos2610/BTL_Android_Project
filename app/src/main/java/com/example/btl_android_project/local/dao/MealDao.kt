package com.example.btl_android_project.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.btl_android_project.local.entity.Meal
import com.example.btl_android_project.local.entity.MealFoodCrossRef
import com.example.btl_android_project.local.entity.MealRecipeCrossRef
import com.example.btl_android_project.local.entity.MealWithFoodsAndRecipes
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    // Insert Meal
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMeal(meal: Meal): Long

    // Update Meal
    @Update
    suspend fun updateMeal(meal: Meal)

    // Delete Meal
    @Delete
    suspend fun deleteMeal(meal: Meal)

    // Insert Food relation
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealFoodCrossRef(ref: MealFoodCrossRef)

    // Insert Recipe relation
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMealRecipeCrossRef(ref: MealRecipeCrossRef)

    // Delete Food relation
    @Query("DELETE FROM meal_food_cross_ref WHERE mealId = :mealId AND foodId = :foodId")
    suspend fun deleteMealFoodCrossRef(mealId: Int, foodId: Int)

    // Delete Recipe relation
    @Query("DELETE FROM meal_recipe_cross_ref WHERE mealId = :mealId AND recipeId = :recipeId")
    suspend fun deleteMealRecipeCrossRef(mealId: Int, recipeId: Int)

    // Get Meal with all its Foods and Recipes
    @Transaction
    @Query("SELECT * FROM meals WHERE id = :mealId")
    suspend fun getMealWithFoodsAndRecipes(mealId: Int): MealWithFoodsAndRecipes

    // Get all meals of a user (with basic info)
    @Query("SELECT * FROM meals WHERE userId = :userId ORDER BY id DESC")
    suspend fun getMealsByUserId(userId: Int): List<Meal>

    // Get all meals of a user with Foods and Recipes
    @Transaction
    @Query("SELECT * FROM meals WHERE userId = :userId ORDER BY id DESC")
    suspend fun getMealsWithFoodsAndRecipesByUser(userId: Int): List<MealWithFoodsAndRecipes>

    @Query("SELECT * FROM meals WHERE id = :mealId")
    suspend fun getMealById(mealId: Int): Meal

    @Query("SELECT * FROM meals")
    fun getMeals(): Flow<List<Meal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMeals(meals: List<Meal>)

    @Query("SELECT * FROM meals WHERE name LIKE '%' || :query || '%'")
    suspend fun searchMeals(query: String): List<Meal>
}
