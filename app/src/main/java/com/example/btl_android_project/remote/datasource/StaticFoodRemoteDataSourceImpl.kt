package com.example.btl_android_project.remote.datasource
import com.example.btl_android_project.remote.Resource
import com.example.btl_android_project.remote.domain.StaticFoodRemoteDataSource
import com.example.btl_android_project.remote.model.StaticFoodListResponse
import com.example.btl_android_project.remote.model.StaticFoodResponse
import com.example.btl_android_project.remote.service.StaticFoodService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StaticFoodRemoteDataSourceImpl @Inject constructor(
    val staticFoodService: StaticFoodService
) : StaticFoodRemoteDataSource {
    override suspend fun getStaticFoodByFoodId(foodId: Long): Resource<StaticFoodResponse> {
        return withContext(Dispatchers.IO) {
            staticFoodService.getStaticFoodByFoodId(foodId)
        }
    }

    override suspend fun getStaticFoodList(
        maxResults: Int,
        pageNumber: Int,
        searchExpression: String
    ): Resource<StaticFoodListResponse> {
        return withContext(Dispatchers.IO) {
            staticFoodService.getStaticFoodList(maxResults, pageNumber, searchExpression)
        }
    }
}