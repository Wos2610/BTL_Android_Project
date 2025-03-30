package com.example.btl_android_project.remote.domain

import com.example.btl_android_project.entity.StaticRecipeIngredient
import com.example.btl_android_project.remote.Resource

interface StaticRecipeIngredientRemoteDataSource {
    suspend fun getStaticRecipeIngredients(
        pageSize: Int,
        pageNumber: Int,
        dataType: String,
    ): Resource<List<StaticRecipeIngredient>>
}