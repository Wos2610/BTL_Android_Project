package com.example.btl_android_project.remote.interceptor
import com.example.btl_android_project.BuildConfig.USDA_API_KEY
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Basic $USDA_API_KEY") // Basic Auth
            .build()
        return chain.proceed(request)
    }
}