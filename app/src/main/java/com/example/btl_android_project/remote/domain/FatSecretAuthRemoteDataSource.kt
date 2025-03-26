package com.example.btl_android_project.remote.domain

import com.example.btl_android_project.remote.AccessTokenResponse
import com.example.btl_android_project.remote.Resource

interface FatSecretAuthRemoteDataSource {
    suspend fun getAccessToken(): Resource<AccessTokenResponse>
}