package com.example.btl_android_project.firestore.di

import com.example.btl_android_project.firestore.datasource.StaticRecipeIngredientFireStoreDataSourceImpl
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
}