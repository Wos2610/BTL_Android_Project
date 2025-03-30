package com.example.btl_android_project.remote.service

import com.example.btl_android_project.remote.Resource
import com.example.btl_android_project.remote.model.StaticRecipeResponse
import com.example.btl_android_project.remote.model.StaticRecipeListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface StaticRecipeService {
    @GET("recipe/v2")
    suspend fun getStaticRecipeByRecipeId(
        @Query("recipe_id") recipeId: Long,
        @Query("format") format: String = "json",
    ): Resource<StaticRecipeResponse>

    @GET("recipes/search/v3")
    suspend fun getStaticRecipeList(
        @Query("max_results") maxResults: Int,
        @Query("page_number") pageNumber: Int,
        @Query("format") format: String = "json",
    ): Resource<StaticRecipeListResponse>
}