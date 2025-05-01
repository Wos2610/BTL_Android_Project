package com.example.btl_android_project.repository

import com.example.btl_android_project.firestore.datasource.StaticRecipeIngredientFireStoreDataSourceImpl
import com.example.btl_android_project.local.dao.StaticRecipeIngredientDao
import com.example.btl_android_project.local.entity.StaticRecipeIngredient
import com.example.btl_android_project.remote.domain.StaticRecipeIngredientRemoteDataSource
import com.example.btl_android_project.remote.onError
import com.example.btl_android_project.remote.onException
import com.example.btl_android_project.remote.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class StaticRecipeIngredientRepository @Inject constructor(
    private val staticRecipeIngredientDao: StaticRecipeIngredientDao,
    private val staticRecipeIngredientRemoteDataSource: StaticRecipeIngredientRemoteDataSource,
    private val staticRecipeIngredientFireStoreDataSource: StaticRecipeIngredientFireStoreDataSourceImpl,
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

    suspend fun pullStaticRecipeIngredientsFromFireStore() {
        val recipeIngredients = staticRecipeIngredientFireStoreDataSource.pullRecipeIngredients()
        Timber.d("Recipe ingredients from Firestore: $recipeIngredients")
        staticRecipeIngredientDao.deleteAllIngredients()
        staticRecipeIngredientDao.insertAllIngredients(recipeIngredients)
    }

    fun getAllRecipeIngredients(): Flow<List<StaticRecipeIngredient>> = staticRecipeIngredientDao.getAllIngredientsFlow()

    fun searchRecipeIngredients(query: String): Flow<List<StaticRecipeIngredient>> = staticRecipeIngredientDao.searchIngredients(query)

    suspend fun getIngredientById(id: Int): StaticRecipeIngredient? {
        return withContext(Dispatchers.IO) {
            val recipeIngredient = staticRecipeIngredientDao.getIngredientByFdcId(id)
            recipeIngredient
        }
    }

}