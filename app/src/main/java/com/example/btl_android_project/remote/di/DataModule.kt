package com.example.btl_android_project.remote.di
import com.example.btl_android_project.remote.datasource.FatSecretAuthRemoteDataSourceImpl
import com.example.btl_android_project.remote.datasource.StaticFoodRemoteDataSourceImpl
import com.example.btl_android_project.remote.datasource.StaticRecipeRemoteDataSourceImpl
import com.example.btl_android_project.remote.datasource.StaticRecipeIngredientRemoteDataSourceImpl
import com.example.btl_android_project.remote.domain.FatSecretAuthRemoteDataSource
import com.example.btl_android_project.remote.domain.StaticFoodRemoteDataSource
import com.example.btl_android_project.remote.domain.StaticRecipeRemoteDataSource
import com.example.btl_android_project.remote.domain.StaticRecipeIngredientRemoteDataSource

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    abstract fun bindStaticRecipeIngredientDataSource(
        staticRecipeIngredientDataSource:StaticRecipeIngredientRemoteDataSourceImpl
    ): StaticRecipeIngredientRemoteDataSource


    @Binds
    abstract fun bindFatSecretAuthDataSource(
        fatSecretAuthDataSource: FatSecretAuthRemoteDataSourceImpl
    ): FatSecretAuthRemoteDataSource

    @Binds
    abstract fun bindRecipeDataSource(
        recipeDataSource: StaticRecipeRemoteDataSourceImpl
    ): StaticRecipeRemoteDataSource

    @Binds
    abstract fun bindFoodDataSource(
        foodDataSource: StaticFoodRemoteDataSourceImpl
    ): StaticFoodRemoteDataSource
}