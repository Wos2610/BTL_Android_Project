package com.example.btl_android_project.firestore.di

import com.example.btl_android_project.firestore.datasource.FoodFireStoreDataSourceImpl
import com.example.btl_android_project.firestore.datasource.MealFireStoreDataSourceImpl
import com.example.btl_android_project.firestore.datasource.MealFoodCrossRefFireStoreDataSourceImpl
import com.example.btl_android_project.firestore.datasource.MealRecipeCrossRefFireStoreDataSourceImpl
import com.example.btl_android_project.firestore.datasource.RecipeFireStoreDataSourceImpl
import com.example.btl_android_project.firestore.datasource.StaticFoodFireStoreDataSourceImpl
import com.example.btl_android_project.firestore.datasource.StaticRecipeFireStoreDataSourceImpl
import com.example.btl_android_project.firestore.datasource.StaticRecipeIngredientFireStoreDataSourceImpl
import com.example.btl_android_project.firestore.domain.FoodFireStoreDataSource
import com.example.btl_android_project.firestore.domain.MealFireStoreDataSource
import com.example.btl_android_project.firestore.domain.MealFoodCrossRefFireStoreDataSource
import com.example.btl_android_project.firestore.domain.MealRecipeCrossRefFireStoreDataSource
import com.example.btl_android_project.firestore.domain.RecipeFireStoreDataSource
import com.example.btl_android_project.firestore.domain.StaticFoodFireStoreDataSource
import com.example.btl_android_project.firestore.domain.StaticRecipeFireStoreDataSource
import com.example.btl_android_project.firestore.domain.StaticRecipeIngredientFireStoreDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FireStoreDataModule {
    @Binds
    abstract fun bindStaticRecipeIngredientFireStoreDataSource(
        staticRecipeIngredientFireStoreDataSourceImpl: StaticRecipeIngredientFireStoreDataSourceImpl
    ): StaticRecipeIngredientFireStoreDataSource

    @Binds
    abstract fun bindStaticRecipeFireStoreDataSource(
        staticRecipeFireStoreDataSourceImpl: StaticRecipeFireStoreDataSourceImpl
    ): StaticRecipeFireStoreDataSource

    @Binds
    abstract fun bindStaticFoodFireStoreDataSource(
        staticFoodFireStoreDataSourceImpl: StaticFoodFireStoreDataSourceImpl
    ): StaticFoodFireStoreDataSource

    @Binds
    abstract fun bindFoodFireStoreDataSource(
        foodFireStoreDataSourceImpl: FoodFireStoreDataSourceImpl
    ): FoodFireStoreDataSource

    @Binds
    abstract fun bindRecipeFireStoreDataSource(
        recipeFireStoreDataSourceImpl: RecipeFireStoreDataSourceImpl
    ): RecipeFireStoreDataSource

    @Binds
    abstract fun bindMealFoodCrossRefFireStoreDataSource(
        mealFoodCrossRefFireStoreDataSourceImpl: MealFoodCrossRefFireStoreDataSourceImpl
    ): MealFoodCrossRefFireStoreDataSource

    @Binds
    abstract fun bindMealRecipeCrossRefFireStoreDataSource(
        mealRecipeCrossRefFireStoreDataSourceImpl: MealRecipeCrossRefFireStoreDataSourceImpl
    ): MealRecipeCrossRefFireStoreDataSource

    @Binds
    abstract fun bindMealFireStoreDataSource(
        mealFireStoreDataSourceImpl: MealFireStoreDataSourceImpl
    ): MealFireStoreDataSource
}