package com.example.btl_android_project.remote.interceptor
import okhttp3.Interceptor
import okhttp3.Response

val USDA_API_KEY = "zpcc8adgTnRmJQSibMaPxSHyGbyoPHjisTDrub0k"
class AuthInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Basic ${USDA_API_KEY}") // Basic Auth
            .build()
        return chain.proceed(request)
    }
}