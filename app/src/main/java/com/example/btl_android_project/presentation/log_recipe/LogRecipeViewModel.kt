package com.example.btl_android_project.presentation.log_recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.local.entity.Recipe
import com.example.btl_android_project.repository.MealRecipeCrossRefRepository
import com.example.btl_android_project.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogRecipeViewModel @Inject constructor(
    val recipeRepository: RecipeRepository,
    val mealRecipeCrossRefRepository: MealRecipeCrossRefRepository,
): ViewModel() {
    private val _recipes = recipeRepository.getAllRecipes()
    val recipes = _recipes

    fun pullRecipesFromFireStore(userId: Int) {
        viewModelScope.launch {
            recipeRepository.pullFromFireStore(userId = userId)
            mealRecipeCrossRefRepository.pullFromFireStore(userId = userId)
        }
    }
}