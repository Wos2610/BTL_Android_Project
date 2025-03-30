package com.example.btl_android_project.remote.datasource

import com.example.btl_android_project.local.entity.StaticRecipeIngredient
import com.example.btl_android_project.remote.Resource
import com.example.btl_android_project.remote.domain.StaticRecipeIngredientRemoteDataSource
import com.example.btl_android_project.remote.service.StaticRecipeIngredientService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StaticRecipeIngredientRemoteDataSourceImpl @Inject constructor(
    private val staticRecipeIngredientService: StaticRecipeIngredientService,
) : StaticRecipeIngredientRemoteDataSource {
    override suspend fun getStaticRecipeIngredients(
        pageSize: Int,
        pageNumber: Int,
        dataType: String,
    ): Resource<List<StaticRecipeIngredient>> {
        return withContext(Dispatchers.IO) {
            staticRecipeIngredientService.getStaticRecipeIngredients(
                pageSize = pageSize,
                pageNumber = pageNumber,
                dataType = dataType,
            )
        }
    }
}