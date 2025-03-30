package com.example.btl_android_project.presentation.dashboard

import androidx.lifecycle.ViewModel
import com.example.btl_android_project.entity.StaticRecipeIngredient
import com.example.btl_android_project.repository.StaticRecipeIngredientRepository
import com.example.btl_android_project.repository.UserRepository
import com.google.common.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.remote.FatSecretTokenManager
import com.example.btl_android_project.remote.domain.FatSecretAuthRemoteDataSource
import com.example.btl_android_project.remote.domain.RecipeRemoteDataSource
import com.example.btl_android_project.remote.onError
import com.example.btl_android_project.remote.onException
import com.example.btl_android_project.remote.onSuccess
import com.google.gson.Gson
import timber.log.Timber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
) : ViewModel() {
    val userRepository = UserRepository()
    @Inject lateinit var staticRecipeIngredientRepository: StaticRecipeIngredientRepository
    @Inject lateinit var recipeRemoteDataSource: RecipeRemoteDataSource
    @Inject lateinit var fatSecretTokenManager: FatSecretTokenManager

    fun pullStaticRecipeIngredients() {
        viewModelScope.launch {
            staticRecipeIngredientRepository.pullStaticRecipeIngredients()
        }
    }

    fun getAccessToken() {
        viewModelScope.launch {
            val token = fatSecretTokenManager.getAccessToken()
            Timber.d("Access Token: $token")
            recipeRemoteDataSource.getStaticRecipe(10)
                .onSuccess {
                    Timber.d("Success: $it")
                }
                .onError {
                    Timber.d("Error: $it")
                }
                .onException {
                    Timber.d("Exception: $it")
                }
        }
    }


}