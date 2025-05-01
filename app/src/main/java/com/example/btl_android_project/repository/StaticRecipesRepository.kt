package com.example.btl_android_project.repository

import com.example.btl_android_project.firestore.datasource.StaticRecipeFireStoreDataSourceImpl
import com.example.btl_android_project.local.dao.StaticRecipeDao
import com.example.btl_android_project.local.entity.StaticRecipesEntity
import com.example.btl_android_project.remote.domain.StaticRecipeRemoteDataSource
import com.example.btl_android_project.remote.onError
import com.example.btl_android_project.remote.onException
import com.example.btl_android_project.remote.onSuccess
import com.example.btl_android_project.utils.mapToStaticRecipeEntity
import com.example.btl_android_project.utils.mapToStaticRecipeEntityList
import com.example.btl_android_project.utils.mapToStaticRecipeList
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject

class StaticRecipesRepository @Inject constructor(
    private val staticRecipeDao: StaticRecipeDao,
    private val staticRecipeRemoteDataSource: StaticRecipeRemoteDataSource,
    private val staticRecipeFireStoreDataSource: StaticRecipeFireStoreDataSourceImpl,
) {
    suspend fun pullStaticRecipes(){
        for(pageNumber in 0..76){
            Timber.d("Page number: $pageNumber")
            delay(1000)
            val staticRecipeListResponse = staticRecipeRemoteDataSource.getStaticRecipeList(PAGE_SIZE, pageNumber)
            staticRecipeListResponse.onSuccess {result ->
                val recipeEntities = mutableListOf<StaticRecipesEntity>()

                result?.recipes?.recipeInRecipesContainerList?.forEach { recipeInRecipesContainer ->
                    val recipeId = recipeInRecipesContainer.id
                    Timber.d("Recipe ID: $recipeId")
                    val staticRecipeResponse = staticRecipeRemoteDataSource.getStaticRecipeByRecipeId(recipeId ?: 0)
                    staticRecipeResponse.onSuccess { recipeResponse ->
                        recipeResponse?.recipe?.let { recipe ->
                            val entity = mapToStaticRecipeEntity(recipe)
                            recipeEntities.add(entity)
                        }
                    }.onError {
                        Timber.e("Error fetching static recipe by ID: $it")
                    }.onException {
                        Timber.e("Exception fetching static recipe by ID: $it")
                    }
                }

                if (recipeEntities.isNotEmpty()) {
                    Timber.d("Inserting ${recipeEntities.size} recipes into the database")
                    staticRecipeDao.insertAllStaticRecipes(recipeEntities)
                } else {
                    Timber.d("No recipes found in the response")
                }
            }.onError {
                Timber.d("pullStaticRecipes: error $it")
            }.onException {
                Timber.d("pullStaticRecipes: exception $it")
            }
        }
    }

    suspend fun pushToFireStore() {
        val recipes = staticRecipeDao.getAllStaticRecipes()
        Timber.d("Pushing ${recipes.size} recipes to Firestore")
        staticRecipeFireStoreDataSource.addAllRecipes(mapToStaticRecipeList(recipes))
    }

    suspend fun pullStaticRecipesFromFireStore() {
        val recipes = staticRecipeFireStoreDataSource.pullRecipes()
        Timber.d("Recipes from Firestore: $recipes")
        staticRecipeDao.insertAllStaticRecipes(mapToStaticRecipeEntityList(recipes))
    }

    companion object {
        private const val PAGE_SIZE = 50
    }
}