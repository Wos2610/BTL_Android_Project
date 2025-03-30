package com.example.btl_android_project.remote.domain

import com.example.btl_android_project.remote.Resource
import com.example.btl_android_project.remote.model.StaticRecipeResponse
import com.example.btl_android_project.remote.model.StaticRecipeListResponse

interface StaticRecipeRemoteDataSource {
    suspend fun getStaticRecipeByRecipeId(
        recipeId: Long,
    ): Resource<StaticRecipeResponse>

    suspend fun getStaticRecipeList(
        maxResults: Int,
        pageNumber: Int,
    ): Resource<StaticRecipeListResponse>
}