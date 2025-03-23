package com.example.btl_android_project.remote.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.btl_android_project.remote.NetworkResultCallAdapterFactory
import com.example.btl_android_project.remote.interceptor.AuthInterceptor
import com.example.btl_android_project.remote.interceptor.BasicAuthInterceptor
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.logging.HttpLoggingInterceptor

val API_URL = "https://api.nal.usda.gov/fdc/"
val API_KEY = "zpcc8adgTnRmJQSibMaPxSHyGbyoPHjisTDrub0k"

@Module
@InstallIn(SingletonComponent::class)
object DataApiModule {
    @Singleton
    @Provides
    fun provideOkHttp(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            protocols(listOf(Protocol.HTTP_1_1, Protocol.HTTP_2))
            addInterceptor(BasicAuthInterceptor(API_KEY, ""))
            addInterceptor(HttpLoggingInterceptor())
            connectTimeout(5, TimeUnit.SECONDS)
            hostnameVerifier { hostname, session -> true }
        }.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().apply {
            baseUrl(API_URL)
            client(okHttpClient)
            addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setLenient()
                        .create()
                )
            )
            addCallAdapterFactory(NetworkResultCallAdapterFactory.create())
        }.build()
    }
}