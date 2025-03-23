package com.example.btl_android_project.remote

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type
class NetworkResultCallAdapter(
  private val resultType: Type
) : CallAdapter<Type, Call<Resource<Type>>> {

  override fun responseType(): Type = resultType

  override fun adapt(call: Call<Type>): Call<Resource<Type>> {
      return NetworkResultCall(call)
  }
}