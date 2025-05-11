package com.example.btl_android_project.local.di

import android.content.Context
import androidx.room.Room
import com.example.btl_android_project.local.AppDatabase
import com.example.btl_android_project.local.dao.DailyDiaryDao
import com.example.btl_android_project.local.dao.DiaryFoodCrossRefDao
import com.example.btl_android_project.local.dao.DiaryMealCrossRefDao
import com.example.btl_android_project.local.dao.DiaryRecipeCrossRefDao
import com.example.btl_android_project.local.dao.FoodDao
import com.example.btl_android_project.local.dao.LogWeightDao
import com.example.btl_android_project.local.dao.MealFoodCrossRefDao
import com.example.btl_android_project.local.dao.MealRecipeCrossRefDao
import com.example.btl_android_project.local.dao.StaticFoodDao
import com.example.btl_android_project.local.dao.StaticRecipeDao
import com.example.btl_android_project.local.dao.StaticRecipeIngredientDao
import com.example.btl_android_project.local.dao.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun provideAssetDao(appDatabase: AppDatabase) : StaticRecipeIngredientDao {
        return appDatabase.staticRecipeIngredientDao()
    }

    @Provides
    fun provideStaticRecipeDao(appDatabase: AppDatabase) : StaticRecipeDao {
        return appDatabase.staticRecipeDao()
    }

    @Provides
    fun provideStaticFoodDao(appDatabase: AppDatabase) : StaticFoodDao {
        return appDatabase.staticFoodDao()
    }

    @Provides
    fun provideFoodDao(appDatabase: AppDatabase): FoodDao {
        return appDatabase.foodDao()
    }

    @Provides
    fun provideLogWeightDao(appDatabase: AppDatabase): LogWeightDao {
        return appDatabase.logWeightDao()
    }

    @Provides
    fun provideRecipeDao(appDatabase: AppDatabase) : com.example.btl_android_project.local.dao.RecipeDao {
        return appDatabase.recipeDao()
    }

    @Provides
    fun provideMealDao(appDatabase: AppDatabase) : com.example.btl_android_project.local.dao.MealDao {
        return appDatabase.mealDao()
    }

    @Provides
    fun provideMealFoodCrossRefDao(appDatabase: AppDatabase) : MealFoodCrossRefDao {
        return appDatabase.mealFoodCrossRefDao()
    }

    @Provides
    fun provideMealRecipeCrossRefDao(appDatabase: AppDatabase) : MealRecipeCrossRefDao {
        return appDatabase.mealRecipeCrossRefDao()
    }

    @Provides
    fun  provideDairyDao(appDatabase: AppDatabase) : DailyDiaryDao {
        return appDatabase.diaryDao()
    }

    @Provides
    fun provideDiaryFoodCrossRefDao(appDatabase: AppDatabase) : DiaryFoodCrossRefDao {
        return appDatabase.diaryFoodCrossRefDao()
    }

    @Provides
    fun provideDiaryRecipeCrossRefDao(appDatabase: AppDatabase) : DiaryRecipeCrossRefDao {
        return appDatabase.diaryRecipeCrossRefDao()
    }

    @Provides
    fun provideDiaryMealCrossRefDao(appDatabase: AppDatabase) : DiaryMealCrossRefDao {
        return appDatabase.diaryMealCrossRefDao()
    }

    @Provides
    fun provideUserProfileDao(appDatabase: AppDatabase) : UserProfileDao {
        return appDatabase.userProfileDao()
    }

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) : AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration()
            .build()
    }
}