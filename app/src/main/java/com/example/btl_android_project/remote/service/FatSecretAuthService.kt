package com.example.btl_android_project.remote.service

import com.example.btl_android_project.remote.AccessTokenResponse
import com.example.btl_android_project.remote.Resource
import retrofit2.http.POST

interface FatSecretAuthService {
    @POST("connect/token")
    suspend fun getAccessToken(): Resource<AccessTokenResponse>
}
