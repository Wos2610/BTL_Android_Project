package com.example.btl_android_project.remote.di
import com.example.btl_android_project.remote.service.FatSecretAuthService
import com.example.btl_android_project.remote.service.StaticRecipeIngredientService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Singleton
    @Provides
    fun provideStaticRecipeIngredientService(@UsdaRetrofit retrofit: Retrofit) : StaticRecipeIngredientService {
        return retrofit.create(StaticRecipeIngredientService::class.java)
    }

    @Singleton
    @Provides
    fun provideFatSecretAuthService(@OAuthFatSecretRetrofit retrofit: Retrofit) : FatSecretAuthService {
        return retrofit.create(FatSecretAuthService::class.java)
    }
}