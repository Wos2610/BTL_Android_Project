package com.example.btl_android_project.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.btl_android_project.local.dao.RecipeDao
import com.example.btl_android_project.local.entity.StaticFoodEntity
import com.example.btl_android_project.local.entity.StaticRecipeIngredient
import com.example.btl_android_project.local.entity.StaticRecipesEntity
import com.example.btl_android_project.local.dao.StaticFoodDao
import com.example.btl_android_project.local.dao.StaticRecipeDao
import com.example.btl_android_project.local.dao.StaticRecipeIngredientDao
import com.example.btl_android_project.local.entity.Recipe
import com.example.btl_android_project.local.entity.*
import com.example.btl_android_project.local.dao.*

@Database(
    entities = [
        StaticRecipeIngredient::class,
        StaticRecipesEntity::class,
        StaticFoodEntity::class,
        Recipe::class,
        Food::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class, RecipeTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun staticRecipeIngredientDao(): StaticRecipeIngredientDao
    abstract fun staticRecipeDao(): StaticRecipeDao
    abstract fun staticFoodDao(): StaticFoodDao
    abstract fun foodDao(): FoodDao
    abstract fun recipeDao(): RecipeDao
}