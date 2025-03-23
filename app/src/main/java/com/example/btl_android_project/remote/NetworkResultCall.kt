package com.example.btl_android_project.remote


import com.example.btl_android_project.remote.NetworkUtils.processCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetworkResultCall<T : Any>(
    private val proxy: Call<T>,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO) // You can choose the appropriate dispatcher
) : Call<Resource<T>> {

    override fun enqueue(callback: Callback<Resource<T>>) {
        proxy.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                coroutineScope.launch {
                    val networkResult = processCall { response }
                    callback.onResponse(this@NetworkResultCall, Response.success(networkResult))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                val networkResult = Resource.Exception<T>(t)
                callback.onResponse(this@NetworkResultCall, Response.success(networkResult))
            }
        })
    }

    override fun execute(): Response<Resource<T>> = throw NotImplementedError()
    override fun clone(): Call<Resource<T>> = NetworkResultCall(proxy.clone())
    override fun request(): Request = proxy.request()
    override fun timeout(): Timeout = proxy.timeout()
    override fun isExecuted(): Boolean = proxy.isExecuted
    override fun isCanceled(): Boolean = proxy.isCanceled
    override fun cancel() { proxy.cancel() }
}