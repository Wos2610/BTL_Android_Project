package com.example.btl_android_project.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.btl_android_project.local.entity.StaticRecipesEntity

@Dao
interface StaticRecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaticRecipe(recipe: StaticRecipesEntity)

    @Query("SELECT * FROM static_recipes WHERE recipeId = :recipeId")
    suspend fun getRecipeById(recipeId: Int): StaticRecipesEntity?

    @Query("SELECT * FROM static_recipes")
    suspend fun getAllStaticRecipes(): List<StaticRecipesEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStaticRecipes(staticRecipes: List<StaticRecipesEntity>)
}