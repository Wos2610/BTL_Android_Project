package com.example.btl_android_project.remote.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.btl_android_project.remote.NetworkResultCallAdapterFactory
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
import com.example.btl_android_project.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
object DataApiModule {
    @Singleton
    @Provides
    @UsdaOkHttp
    fun provideUsdaOkHttp(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            protocols(listOf(Protocol.HTTP_1_1, Protocol.HTTP_2))
            addInterceptor(BasicAuthInterceptor(BuildConfig.USDA_API_KEY, ""))
            addInterceptor(HttpLoggingInterceptor())
            connectTimeout(5, TimeUnit.SECONDS)
            hostnameVerifier { hostname, session -> true }
        }.build()
    }

    @Singleton
    @Provides
    @FatSecretOkHttp
    fun provideFatSecretOkHttp(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            protocols(listOf(Protocol.HTTP_1_1, Protocol.HTTP_2))
            addInterceptor(BasicAuthInterceptor(BuildConfig.USDA_API_KEY, ""))
            addInterceptor(HttpLoggingInterceptor())
            connectTimeout(5, TimeUnit.SECONDS)
            hostnameVerifier { hostname, session -> true }
        }.build()
    }

    @Singleton
    @Provides
    @OAuthFatSecretOkhttp
    fun provideOAuthFatSecretOkHttp(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            protocols(listOf(Protocol.HTTP_1_1, Protocol.HTTP_2))
            addInterceptor(BasicAuthInterceptor(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET))
            addInterceptor(HttpLoggingInterceptor())
            connectTimeout(5, TimeUnit.SECONDS)
            hostnameVerifier { hostname, session -> true }
        }.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Singleton
    @Provides
    @UsdaRetrofit
    fun provideUsdaRetrofit(@UsdaOkHttp okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().apply {
            baseUrl(BuildConfig.USDA_API_URL)
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

    @RequiresApi(Build.VERSION_CODES.O)
    @Singleton
    @Provides
    @FatSecretRetrofit
    fun provideFatSecretRetrofit(@FatSecretOkHttp okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().apply {
            baseUrl(BuildConfig.FAT_SECRET_API_URL)
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

    @RequiresApi(Build.VERSION_CODES.O)
    @Singleton
    @Provides
    @OAuthFatSecretRetrofit
    fun provideOAuthFatSecretRetrofit(@OAuthFatSecretOkhttp okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().apply {
            baseUrl(BuildConfig.OAUTH_FAT_SECRET_API_URL)
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