package com.example.btl_android_project.presentation.log_recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.local.entity.Recipe
import com.example.btl_android_project.repository.MealRecipeCrossRefRepository
import com.example.btl_android_project.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogRecipeViewModel @Inject constructor(
    val recipeRepository: RecipeRepository,
    val mealRecipeCrossRefRepository: MealRecipeCrossRefRepository,
): ViewModel() {
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes = _recipes.asStateFlow()
    var userId: Int = 0

    fun pullRecipesFromFireStore() {
        viewModelScope.launch {
            recipeRepository.pullFromFireStore(userId = userId)
            mealRecipeCrossRefRepository.pullFromFireStore(userId = userId)
        }
    }

    fun loadRecipes() {
        viewModelScope.launch {
            val allRecipes = recipeRepository.getRecipesByUserId(userId)
            _recipes.value = allRecipes
        }
    }

    fun searchRecipes(query: String) {
        viewModelScope.launch {
            val results = recipeRepository.searchRecipes(query, userId)
            _recipes.value = results
        }
    }
}