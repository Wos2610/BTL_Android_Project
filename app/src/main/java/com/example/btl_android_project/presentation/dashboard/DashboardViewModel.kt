package com.example.btl_android_project.presentation.dashboard

import androidx.lifecycle.ViewModel
import com.example.btl_android_project.repository.StaticRecipeIngredientRepository
import com.example.btl_android_project.repository.UserRepository
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.remote.FatSecretTokenManager
import com.example.btl_android_project.remote.domain.StaticRecipeRemoteDataSource
import com.example.btl_android_project.remote.onError
import com.example.btl_android_project.remote.onException
import com.example.btl_android_project.remote.onSuccess
import com.example.btl_android_project.repository.StaticFoodsRepository
import com.example.btl_android_project.repository.StaticRecipesRepository
import timber.log.Timber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
) : ViewModel() {
    val userRepository = UserRepository()
    @Inject lateinit var staticRecipeIngredientRepository: StaticRecipeIngredientRepository
    @Inject lateinit var staticRecipesRepository: StaticRecipesRepository
    @Inject lateinit var staticFoodRepository: StaticFoodsRepository

    fun pullStaticRecipeIngredients() {
        viewModelScope.launch {
            staticRecipeIngredientRepository.pullStaticRecipeIngredients()
        }
    }

    fun pullStaticRecipes() {
        viewModelScope.launch {
            staticRecipesRepository.pullStaticRecipes()
        }
    }

    fun pullStaticFoods() {
        viewModelScope.launch {
            staticFoodRepository.pullStaticFoods()
        }
    }

    fun pushStaticFoodsToFireStore() {
        viewModelScope.launch {
            staticFoodRepository.pushToFireStore()
        }
    }


}