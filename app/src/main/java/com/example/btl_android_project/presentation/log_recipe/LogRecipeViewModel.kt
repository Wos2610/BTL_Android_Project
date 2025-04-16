package com.example.btl_android_project.presentation.log_recipe

import androidx.lifecycle.ViewModel
import com.example.btl_android_project.local.entity.Recipe
import com.example.btl_android_project.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogRecipeViewModel @Inject constructor(
    val recipeRepository: RecipeRepository
): ViewModel() {
    private val _recipes = recipeRepository.getAllRecipes()
    val recipes = _recipes
}