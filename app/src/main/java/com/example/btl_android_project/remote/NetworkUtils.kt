package com.example.btl_android_project.remote

import android.util.Log
import retrofit2.Response
import timber.log.Timber

object NetworkUtils {
    suspend fun <T> processCall(fetchData: suspend () -> Response<T>) : Resource<T> {
        return try{
            val response = fetchData.invoke()
            println("body: ${response.body()}")
            if(response.isSuccessful){
                Log.d(TAG, "processCall success ${response.raw()}")
                Timber.d("processCall success")
                Resource.Success(response.body())
            }
            else{
                Timber.d("processCall fail/error: ${response.code()} ${response.body()}")
                Resource.DataError(response.code())
            }
        } catch (e: Exception){
            Timber.d("processCall exception: ${e.message}")
            e.printStackTrace()
            Resource.Exception(e)
        }
    }

    private const val TAG = "NetworkUtils"
}