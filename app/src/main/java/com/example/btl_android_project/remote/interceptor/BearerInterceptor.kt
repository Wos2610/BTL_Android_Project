package com.example.btl_android_project.remote.interceptor

import android.util.Log
import com.example.btl_android_project.remote.FatSecretTokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

class BearerInterceptor @Inject constructor(
    private val tokenManager: FatSecretTokenManager,
    private val coroutineScope: CoroutineScope
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Create a CompletableFuture to handle async token retrieval
        val tokenFuture = CompletableFuture<String>()

        // Launch a coroutine to fetch the token
        coroutineScope.launch {
            try {
                val token = tokenManager.getAccessToken()
                tokenFuture.complete(token)
                Log.d("BearerInterceptor", "Token fetched: $token")
            } catch (e: Exception) {
                tokenFuture.completeExceptionally(e)
            }
        }

        // Wait for the token (without blocking the thread)
        val token = tokenFuture.get()

        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}