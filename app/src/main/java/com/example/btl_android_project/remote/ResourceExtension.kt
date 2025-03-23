package com.example.btl_android_project.remote

suspend fun <T : Any> Resource<T>.onSuccess(
    executable: suspend (T?) -> Unit
): Resource<T> = apply {
    if (this is Resource.Success<T>) {
        executable(data)
    }
}

suspend fun <T : Any> Resource<T?>.onSuccessNullable(
    executable: suspend (T?) -> Unit
): Resource<T?> = apply {
    if (this is Resource.Success<T?>) {
        executable(data)
    }
}

suspend fun <T : Any> Resource<T>.onError(
    executable: suspend (code: Int?) -> Unit
): Resource<T> = apply {
    if (this is Resource.DataError<T>) {
        executable(errorCode)
    }
}

suspend fun <T : Any> Resource<T?>.onErrorNullable(
    executable: suspend (code: Int?) -> Unit
): Resource<T?> = apply {
    if (this is Resource.DataError<T?>) {
        executable(errorCode)
    }
}

suspend fun <T : Any> Resource<T>.onException(
    executable: suspend (e: Throwable) -> Unit
): Resource<T> = apply {
    if (this is Resource.Exception<T>) {
        executable(e)
    }
}

suspend fun <T : Any> Resource<T?>.onExceptionNullable(
    executable: suspend (e: Throwable) -> Unit
): Resource<T?> = apply {
    if (this is Resource.Exception<T?>) {
        executable(e)
    }
}