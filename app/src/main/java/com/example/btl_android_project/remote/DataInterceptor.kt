package com.example.btl_android_project.remote
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.net.SocketTimeoutException
class DataInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            val newRequest : Request = chain.request().newBuilder().header("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $BEARER_TOKEN").build()
            return chain.proceed(newRequest)
        }
        catch (e: Exception){
            e.printStackTrace()
            val code : Int = when(e){
                is SocketTimeoutException -> 500
                else -> 404
            }
            val protocol = chain.connection()?.protocol() ?: Protocol.HTTP_1_1
            return Response.Builder()
                .code(code)
                .message(e.message ?: "Unknown Error")
                .request(chain.request())
                .protocol(protocol)
                .body("$e".toResponseBody(null))
                .build()
        }
    }

    companion object{
        val BEARER_TOKEN = "prdcv_secret_token"
    }
}