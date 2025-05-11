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
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMeal(meal: Meal): Long

    @Update
    suspend fun updateMeal(meal: Meal)

    @Delete
    suspend fun deleteMeal(meal: Meal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealFoodCrossRef(ref: MealFoodCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMealRecipeCrossRef(ref: MealRecipeCrossRef)

    @Query("DELETE FROM meal_food_cross_ref WHERE mealId = :mealId AND foodId = :foodId")
    suspend fun deleteMealFoodCrossRef(mealId: Int, foodId: Int)

    @Query("DELETE FROM meal_recipe_cross_ref WHERE mealId = :mealId AND recipeId = :recipeId")
    suspend fun deleteMealRecipeCrossRef(mealId: Int, recipeId: Int)

    @Transaction
    @Query("SELECT * FROM meals WHERE id = :mealId")
    suspend fun getMealWithFoodsAndRecipes(mealId: Int): MealWithFoodsAndRecipes

    @Query("SELECT * FROM meals WHERE userId = :userId ORDER BY id DESC")
    fun getMealsByUserId(userId: String): Flow<List<Meal>>

    @Transaction
    @Query("SELECT * FROM meals WHERE userId = :userId ORDER BY id DESC")
    suspend fun getMealsWithFoodsAndRecipesByUser(userId: String): List<MealWithFoodsAndRecipes>

    @Query("SELECT * FROM meals WHERE id = :mealId")
    suspend fun getMealById(mealId: String): Meal?

    @Query("SELECT * FROM meals")
    fun getMeals(): Flow<List<Meal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMeals(meals: List<Meal>)

    @Query("SELECT * FROM meals WHERE name LIKE '%' || :query || '%' AND userId = :userId")
    suspend fun searchMeals(query: String, userId: String): List<Meal>

    @Query("DELETE FROM meals")
    suspend fun deleteAllMeals(): Int

}
