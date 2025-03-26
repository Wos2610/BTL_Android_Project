package com.example.btl_android_project.remote.interceptor

import android.os.Build
import androidx.annotation.RequiresApi
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class BasicAuthInterceptor(private val clientId: String,
                           private val clientSecret: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val credentials = "$clientId:$clientSecret"
        val encodedCredentials = Base64.getEncoder().encodeToString(
            credentials.toByteArray(Charsets.UTF_8)
        )
        val originalRequest = chain.request()
        val originalBody = originalRequest.body as? FormBody
        val newFormBodyBuilder = FormBody.Builder()
        originalBody?.let { formBody ->
            for (i in 0 until formBody.size) {
                newFormBodyBuilder.add(formBody.name(i), formBody.value(i))
            }
        }
        newFormBodyBuilder.add("grant_type", GRAND_TYPE)
        newFormBodyBuilder.add("scope", SCOPE)

        val authenticatedRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Basic $encodedCredentials")
            .method(originalRequest.method, newFormBodyBuilder.build())
            .build()

        return chain.proceed(authenticatedRequest)
    }

    companion object{
        private const val SCOPE = "basic"
        private const val GRAND_TYPE = "client_credentials"
    }
}