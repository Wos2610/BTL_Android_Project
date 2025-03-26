package com.example.btl_android_project.remote.datasource

import com.example.btl_android_project.remote.AccessTokenResponse
import com.example.btl_android_project.remote.Resource
import com.example.btl_android_project.remote.domain.FatSecretAuthRemoteDataSource
import com.example.btl_android_project.remote.service.FatSecretAuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FatSecretAuthRemoteDataSourceImpl @Inject constructor(
    private val fatSecretAuthService: FatSecretAuthService,
) : FatSecretAuthRemoteDataSource {
    override suspend fun getAccessToken(): Resource<AccessTokenResponse> {
        return withContext(Dispatchers.IO) {
            fatSecretAuthService.getAccessToken()
        }
    }
}