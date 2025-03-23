package com.example.btl_android_project.remote.interceptor

import android.os.Build
import androidx.annotation.RequiresApi
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class BasicAuthInterceptor(username: String, password: String) : Interceptor {
    private val credentials = "Basic " + Base64.getEncoder().encodeToString("$username:$password".toByteArray())

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", credentials)
            .build()
        return chain.proceed(request)
    }
}