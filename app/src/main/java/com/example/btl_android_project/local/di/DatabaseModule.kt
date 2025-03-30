package com.example.btl_android_project.local.di

import android.content.Context
import androidx.room.Room
import com.example.btl_android_project.local.AppDatabase
import com.example.btl_android_project.local.dao.StaticFoodDao
import com.example.btl_android_project.local.dao.StaticRecipeDao
import com.example.btl_android_project.local.dao.StaticRecipeIngredientDao
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