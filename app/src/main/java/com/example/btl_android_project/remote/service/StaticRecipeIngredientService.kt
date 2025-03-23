package com.example.btl_android_project.remote.service

import com.example.btl_android_project.entity.StaticRecipeIngredient
import com.example.btl_android_project.remote.Resource
import retrofit2.http.GET

interface StaticRecipeIngredientService {
    @GET("v1/foods/list")
    suspend fun getStaticRecipeIngredients(): Resource<List<StaticRecipeIngredient>>
}