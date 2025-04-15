package com.example.btl_android_project.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.btl_android_project.local.entity.StaticRecipeIngredient
import kotlinx.coroutines.flow.Flow

@Dao
interface StaticRecipeIngredientDao {

    // Insert a single ingredient
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: StaticRecipeIngredient)

    // Insert multiple ingredients
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllIngredients(ingredients: List<StaticRecipeIngredient>)

    // Get all ingredients
    @Query("SELECT * FROM static_recipe_ingredients")
    suspend fun getAllIngredients(): List<StaticRecipeIngredient>

    // Get an ingredient by ID
    @Query("SELECT * FROM static_recipe_ingredients WHERE id = :ingredientId")
    suspend fun getIngredientById(ingredientId: Int): StaticRecipeIngredient?

    // Delete all ingredients
    @Query("DELETE FROM static_recipe_ingredients")
    suspend fun deleteAllIngredients()

    @Query("SELECT * FROM static_recipe_ingredients")
    fun getAllIngredientsFlow() : Flow<List<StaticRecipeIngredient>>

    @Query("SELECT * FROM static_recipe_ingredients WHERE description LIKE '%' || :query || '%'")
    fun searchIngredients(query: String): Flow<List<StaticRecipeIngredient>>

    @Query("SELECT * FROM static_recipe_ingredients WHERE fdcId = :ingredientId")
    suspend fun getIngredientByFdcId(ingredientId: Int): StaticRecipeIngredient?
}
