package com.example.btl_android_project.remote.datasource

import com.example.btl_android_project.remote.Resource
import com.example.btl_android_project.remote.domain.StaticRecipeRemoteDataSource
import com.example.btl_android_project.remote.model.StaticRecipeListResponse
import com.example.btl_android_project.remote.model.StaticRecipeResponse
import com.example.btl_android_project.remote.service.StaticRecipeService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StaticRecipeRemoteDataSourceImpl @Inject constructor(
    val staticRecipeService: StaticRecipeService
) : StaticRecipeRemoteDataSource {
    override suspend fun getStaticRecipeByRecipeId(recipeId: Long): Resource<StaticRecipeResponse> {
        return withContext(Dispatchers.IO) {
            staticRecipeService.getStaticRecipeByRecipeId(recipeId)
        }
    }

    override suspend fun getStaticRecipeList(
        maxResults: Int,
        pageNumber: Int
    ): Resource<StaticRecipeListResponse> {
        return withContext(Dispatchers.IO) {
            staticRecipeService.getStaticRecipeList(maxResults, pageNumber)
        }
    }
}