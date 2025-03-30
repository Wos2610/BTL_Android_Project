package com.example.btl_android_project.remote.domain

import com.example.btl_android_project.entity.StaticRecipe
import com.example.btl_android_project.remote.Resource
import com.example.btl_android_project.remote.datasource.StaticRecipeResponse

interface RecipeRemoteDataSource {
    suspend fun getStaticRecipe(
        recipeId: Int,
    ): Resource<StaticRecipeResponse>
}