package com.example.btl_android_project.firestore.di

import com.example.btl_android_project.firestore.datasource.StaticFoodFireStoreDataSourceImpl
import com.example.btl_android_project.firestore.datasource.StaticRecipeFireStoreDataSourceImpl
import com.example.btl_android_project.firestore.datasource.StaticRecipeIngredientFireStoreDataSourceImpl
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
    abstract fun bindRecipeFireStoreDataSource(
        recipeFireStoreDataSourceImpl: com.example.btl_android_project.firestore.datasource.RecipeFireStoreDataSourceImpl
    ): com.example.btl_android_project.firestore.domain.RecipeFireStoreDataSource
}