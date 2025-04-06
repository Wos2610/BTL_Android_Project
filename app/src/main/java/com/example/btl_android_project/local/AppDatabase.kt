package com.example.btl_android_project.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.btl_android_project.local.entity.*
import com.example.btl_android_project.local.dao.*

@Database(
    entities = [
        StaticRecipeIngredient::class,
        StaticRecipesEntity::class,
        StaticFoodEntity::class,
        Food::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class, RecipeTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun staticRecipeIngredientDao(): StaticRecipeIngredientDao
    abstract fun staticRecipeDao(): StaticRecipeDao
    abstract fun staticFoodDao(): StaticFoodDao
    abstract fun foodDao(): FoodDao
}