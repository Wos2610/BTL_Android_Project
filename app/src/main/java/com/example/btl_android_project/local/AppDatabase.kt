package com.example.btl_android_project.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.btl_android_project.local.entity.StaticFoodEntity
import com.example.btl_android_project.local.entity.StaticRecipeIngredient
import com.example.btl_android_project.local.entity.StaticRecipesEntity
import com.example.btl_android_project.local.dao.StaticFoodDao
import com.example.btl_android_project.local.dao.StaticRecipeDao
import com.example.btl_android_project.local.dao.StaticRecipeIngredientDao

@Database(entities = [StaticRecipeIngredient::class, StaticRecipesEntity::class, StaticFoodEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class, RecipeTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun staticRecipeIngredientDao(): StaticRecipeIngredientDao
    abstract fun staticRecipeDao(): StaticRecipeDao
    abstract fun staticFoodDao(): StaticFoodDao
}