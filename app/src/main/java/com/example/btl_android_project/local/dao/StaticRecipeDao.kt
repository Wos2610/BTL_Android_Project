package com.example.btl_android_project.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.btl_android_project.entity.StaticRecipe

@Dao
interface StaticRecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaticRecipe(recipe: StaticRecipe)

    @Query("SELECT * FROM static_recipes WHERE recipe_id = :recipeId")
    suspend fun getRecipeById(recipeId: Int): StaticRecipe?

    @Query("SELECT * FROM static_recipes")
    suspend fun getAllRecipes(): List<StaticRecipe>
}