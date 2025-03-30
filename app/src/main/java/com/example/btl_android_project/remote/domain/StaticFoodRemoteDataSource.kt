package com.example.btl_android_project.remote.domain

import com.example.btl_android_project.remote.Resource
import com.example.btl_android_project.remote.model.StaticFoodListResponse
import com.example.btl_android_project.remote.model.StaticFoodResponse

interface StaticFoodRemoteDataSource {
    suspend fun getStaticFoodByFoodId(
        foodId: Long,
    ): Resource<StaticFoodResponse>

    suspend fun getStaticFoodList(
        maxResults: Int,
        pageNumber: Int,
        searchExpression: String,
    ): Resource<StaticFoodListResponse>
}