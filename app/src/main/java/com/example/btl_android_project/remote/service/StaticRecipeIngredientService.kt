package com.example.btl_android_project.remote.service

import com.example.btl_android_project.entity.StaticRecipeIngredient
import com.example.btl_android_project.remote.Resource
import retrofit2.http.GET
import retrofit2.http.Query

interface StaticRecipeIngredientService {
    @GET("v1/foods/list")
    suspend fun getStaticRecipeIngredients(
        @Query("pageSize") pageSize: Int,
        @Query("pageNumber") pageNumber: Int,
        @Query("dataType") dataType: String,
    ): Resource<List<StaticRecipeIngredient>>
}