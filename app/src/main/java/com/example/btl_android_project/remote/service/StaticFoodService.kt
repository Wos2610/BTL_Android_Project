package com.example.btl_android_project.remote.service
import com.example.btl_android_project.remote.Resource
import com.example.btl_android_project.remote.model.StaticFoodListResponse
import com.example.btl_android_project.remote.model.StaticFoodResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface StaticFoodService {
    @GET("food/v4")
    suspend fun getStaticFoodByFoodId(
        @Query("food_id") foodId: Long,
        @Query("format") format: String = "json",
    ): Resource<StaticFoodResponse>

    @GET("foods/search/v1")
    suspend fun getStaticFoodList(
        @Query("max_results") maxResults: Int,
        @Query("page_number") pageNumber: Int,
        @Query("search_expression") searchExpression: String,
        @Query("format") format: String = "json",
    ): Resource<StaticFoodListResponse>
}