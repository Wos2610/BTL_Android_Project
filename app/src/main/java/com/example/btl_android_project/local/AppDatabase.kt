package com.example.btl_android_project.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.btl_android_project.entity.StaticRecipe
import com.example.btl_android_project.entity.StaticRecipeIngredient
import com.example.btl_android_project.local.dao.StaticRecipeDao
import com.example.btl_android_project.local.dao.StaticRecipeIngredientDao

@Database(entities = [StaticRecipeIngredient::class, StaticRecipe::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun staticRecipeIngredientDao(): StaticRecipeIngredientDao
    abstract fun staticRecipeDao(): StaticRecipeDao
}