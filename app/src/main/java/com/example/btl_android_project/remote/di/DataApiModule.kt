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
import com.example.btl_android_project.entity.Direction
import com.example.btl_android_project.entity.Ingredient
import com.example.btl_android_project.entity.RecipeCategory
import com.example.btl_android_project.remote.FatSecretTokenManager
import com.example.btl_android_project.remote.interceptor.BearerInterceptor
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import kotlinx.coroutines.CoroutineScope

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
    fun provideFatSecretOkHttp(
        bearerInterceptor: BearerInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder().apply {
            protocols(listOf(Protocol.HTTP_1_1))
            addInterceptor(bearerInterceptor)
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
                RetrofitGsonConfig.createGsonConverterFactory()
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

class RetrofitGsonConfig {
    companion object {
        fun createGsonConverterFactory(): GsonConverterFactory {
            val gson = GsonBuilder()
                .setLenient()
                // Explicit type adapters with full type specification
                .registerTypeAdapter(
                    object : TypeToken<List<String>>() {}.type,
                    createListTypeAdapter(String::class.java)
                )
                .registerTypeAdapter(
                    object : TypeToken<List<RecipeCategory>>() {}.type,
                    createListTypeAdapter(RecipeCategory::class.java)
                )
                .registerTypeAdapter(
                    object : TypeToken<List<Ingredient>>() {}.type,
                    createListTypeAdapter(Ingredient::class.java)
                )
                .registerTypeAdapter(
                    object : TypeToken<List<Direction>>() {}.type,
                    createListTypeAdapter(Direction::class.java)
                )
                .create()

            return GsonConverterFactory.create(gson)
        }

        // Generic type adapter with explicit type handling
        private fun <T> createListTypeAdapter(
            elementClass: Class<T>
        ): TypeAdapter<List<T>> {
            return object : TypeAdapter<List<T>>() {
                override fun write(out: JsonWriter, value: List<T>?) {
                    if (value == null) {
                        out.nullValue()
                        return
                    }
                    out.beginArray()
                    value.forEach { item ->
                        when (item) {
                            is String -> out.value(item)
                            else -> Gson().toJson(item, elementClass, out)
                        }
                    }
                    out.endArray()
                }

                override fun read(input: JsonReader): List<T>? {
                    val list = mutableListOf<T>()

                    input.beginArray()
                    while (input.hasNext()) {
                        val item: T = when (elementClass) {
                            String::class.java -> input.nextString() as T
                            else -> Gson().fromJson(input, elementClass)
                        }
                        list.add(item)
                    }
                    input.endArray()

                    return list
                }
            }
        }

        // Additional helper methods for specific conversions
        fun createCustomGson(): Gson {
            return GsonBuilder()
                .setLenient()
                .create()
        }
    }
}