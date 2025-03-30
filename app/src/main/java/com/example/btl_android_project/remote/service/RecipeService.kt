package com.example.btl_android_project.remote.service

import com.example.btl_android_project.entity.StaticRecipe
import com.example.btl_android_project.entity.StaticRecipeIngredient
import com.example.btl_android_project.remote.Resource
import com.example.btl_android_project.remote.datasource.StaticRecipeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeService {
    @GET("recipe/v2/")
    suspend fun getStaticRecipeIngredients(
        @Query("recipe_id") recipeId: Int,
        @Query("format") format: String = "json",
    ): Resource<StaticRecipeResponse>
}