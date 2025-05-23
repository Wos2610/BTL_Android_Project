package com.example.btl_android_project.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.btl_android_project.local.entity.MealRecipeCrossRef

@Dao
interface MealRecipeCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealRecipeCrossRef(food: MealRecipeCrossRef)

    @Query("SELECT * FROM meal_recipe_cross_ref")
    suspend fun getAllMealRecipeCrossRefs(): List<MealRecipeCrossRef>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMealRecipeCrossRefs(mealRecipeCrossRefs: List<MealRecipeCrossRef>)

    @Query("DELETE FROM meal_recipe_cross_ref WHERE recipeId = :recipeId")
    suspend fun deleteMealRecipeCrossRefByRecipeId(recipeId: String)

    @Query("SELECT * FROM meal_recipe_cross_ref WHERE mealId = :mealId")
    suspend fun getMealRecipeCrossRefById(mealId: String): List<MealRecipeCrossRef>?

    @Query("DELETE FROM meal_recipe_cross_ref WHERE mealId = :mealId")
    suspend fun deleteMealRecipeCrossRefByMealId(mealId: String)

    @Query("DELETE FROM meal_recipe_cross_ref")
    suspend fun deleteAllMealRecipeCrossRefs()

    @Query("SELECT * FROM meal_recipe_cross_ref WHERE recipeId = :recipeId")
    suspend fun getMealRecipeCrossRefByRecipeId(recipeId: String): List<MealRecipeCrossRef>?

    @Query("DELETE FROM meal_recipe_cross_ref WHERE mealId = :mealId")
    fun deletesByMealId(mealId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(crossRefs: List<MealRecipeCrossRef>)

    @Transaction
    suspend fun deleteAndInsertInTransaction(mealId: String, crossRefs: List<MealRecipeCrossRef>) {
        deletesByMealId(mealId)
        if (crossRefs.isNotEmpty()) {
            insertAll(crossRefs)
        }
    }

    @Query("DELETE FROM meal_food_cross_ref WHERE mealId IN (:mealIds)")
    suspend fun deleteAllForMeals(mealIds: List<String>)
}