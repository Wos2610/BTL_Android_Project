package com.example.btl_android_project.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.btl_android_project.local.dao.DailyDiaryDao
import com.example.btl_android_project.local.dao.DiaryFoodCrossRefDao
import com.example.btl_android_project.local.dao.DiaryMealCrossRefDao
import com.example.btl_android_project.local.dao.DiaryRecipeCrossRefDao
import com.example.btl_android_project.local.dao.FoodDao
import com.example.btl_android_project.local.dao.MealDao
import com.example.btl_android_project.local.dao.MealFoodCrossRefDao
import com.example.btl_android_project.local.dao.MealRecipeCrossRefDao
import com.example.btl_android_project.local.dao.RecipeDao
import com.example.btl_android_project.local.dao.StaticFoodDao
import com.example.btl_android_project.local.dao.StaticRecipeDao
import com.example.btl_android_project.local.dao.StaticRecipeIngredientDao
import com.example.btl_android_project.local.entity.DailyDiary
import com.example.btl_android_project.local.entity.DiaryFoodCrossRef
import com.example.btl_android_project.local.entity.DiaryMealCrossRef
import com.example.btl_android_project.local.entity.DiaryRecipeCrossRef
import com.example.btl_android_project.local.entity.Food
import com.example.btl_android_project.local.entity.Meal
import com.example.btl_android_project.local.entity.MealFoodCrossRef
import com.example.btl_android_project.local.entity.MealRecipeCrossRef
import com.example.btl_android_project.local.entity.Recipe
import com.example.btl_android_project.local.entity.StaticFoodEntity
import com.example.btl_android_project.local.entity.StaticRecipeIngredient
import com.example.btl_android_project.local.entity.StaticRecipesEntity

@Database(
    entities = [
        StaticRecipeIngredient::class,
        StaticRecipesEntity::class,
        StaticFoodEntity::class,
        Recipe::class,
        Food::class,
        Meal::class,
        MealFoodCrossRef::class,
        MealRecipeCrossRef::class,
        DailyDiary::class,
        DiaryFoodCrossRef::class,
        DiaryRecipeCrossRef::class,
        DiaryMealCrossRef::class,
               ],
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
    abstract fun mealDao(): MealDao
    abstract fun mealFoodCrossRefDao(): MealFoodCrossRefDao
    abstract fun mealRecipeCrossRefDao(): MealRecipeCrossRefDao
    abstract fun diaryDao(): DailyDiaryDao
    abstract fun diaryFoodCrossRefDao(): DiaryFoodCrossRefDao
    abstract fun diaryRecipeCrossRefDao(): DiaryRecipeCrossRefDao
    abstract fun diaryMealCrossRefDao(): DiaryMealCrossRefDao
}