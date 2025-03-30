package com.example.btl_android_project.repository

import com.example.btl_android_project.firestore.domain.StaticRecipeIngredientFireStoreDataSource
import com.example.btl_android_project.local.dao.StaticRecipeIngredientDao
import com.example.btl_android_project.remote.domain.StaticRecipeIngredientRemoteDataSource
import com.example.btl_android_project.remote.onError
import com.example.btl_android_project.remote.onException
import com.example.btl_android_project.remote.onSuccess
import timber.log.Timber
import javax.inject.Inject

class StaticRecipeIngredientRepository @Inject constructor(
    private val staticRecipeIngredientDao: StaticRecipeIngredientDao,
    private val staticRecipeIngredientRemoteDataSource: StaticRecipeIngredientRemoteDataSource,
    private val staticRecipeIngredientFireStoreDataSource: StaticRecipeIngredientFireStoreDataSource,
) {
    suspend fun pullStaticRecipeIngredients(){
        for (pageIndex in 1..2) {
            val staticRecipeIngredients = staticRecipeIngredientRemoteDataSource.getStaticRecipeIngredients(
                pageSize = 200,
                pageNumber = pageIndex,
                dataType = "Foundation"
            )

            staticRecipeIngredients.onSuccess {data ->
                data?.let {
                    staticRecipeIngredientDao.insertAllIngredients(it)
                    staticRecipeIngredientFireStoreDataSource.addAllRecipeIngredients(data)
                }
            }.onError {error ->
                Timber.e("Error getting static recipe ingredients: ${error}")
            }.onException {exception ->
                Timber.e("Exception getting static recipe ingredients: ${exception}")
            }
        }
    }
}