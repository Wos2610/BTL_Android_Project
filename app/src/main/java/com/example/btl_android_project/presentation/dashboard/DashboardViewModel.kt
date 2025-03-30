package com.example.btl_android_project.presentation.dashboard

import androidx.lifecycle.ViewModel
import com.example.btl_android_project.repository.StaticRecipeIngredientRepository
import com.example.btl_android_project.repository.UserRepository
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.repository.StaticFoodsRepository
import com.example.btl_android_project.repository.StaticRecipesRepository
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

    fun loadDataFromFireStore() {
        viewModelScope.launch {
            staticFoodRepository.pullFromFireStore()
            staticRecipeIngredientRepository.pullStaticRecipeIngredientsFromFireStore()
            staticRecipesRepository.pullStaticRecipesFromFireStore()
        }
    }


}