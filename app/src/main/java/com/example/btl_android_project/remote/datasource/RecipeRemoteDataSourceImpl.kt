package com.example.btl_android_project.remote.datasource

import com.example.btl_android_project.entity.StaticRecipe
import com.example.btl_android_project.remote.Resource
import com.example.btl_android_project.remote.domain.RecipeRemoteDataSource
import com.example.btl_android_project.remote.service.RecipeService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecipeRemoteDataSourceImpl @Inject constructor(
    val recipeService: RecipeService
) : RecipeRemoteDataSource {
    override suspend fun getStaticRecipe(recipeId: Int): Resource<StaticRecipeResponse> {
        return withContext(Dispatchers.IO) {
            recipeService.getStaticRecipeIngredients(recipeId)
        }
    }
}