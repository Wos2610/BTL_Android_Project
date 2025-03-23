package com.example.btl_android_project.remote

sealed class Resource<T>(
    val data: T? = null,
    val errorCode: Int? = null,
){
    class Success<T>(data: T?) : Resource<T>(data, null)
    class DataError<T>(errorCode: Int?) : Resource<T>(null, errorCode)
    class Exception<T>(val e: Throwable) : Resource<T>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is DataError -> "Error[error=$errorCode]"
            is Exception -> "Exception"
        }
    }
}