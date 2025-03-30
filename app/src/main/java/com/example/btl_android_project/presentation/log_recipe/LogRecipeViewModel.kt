package com.example.btl_android_project.presentation.log_recipe

import androidx.lifecycle.ViewModel
import com.example.btl_android_project.local.entity.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogRecipeViewModel @Inject constructor(): ViewModel() {
    val recipes = listOf(
        Recipe(id = 1, name = "Recipe 1", calories = 100f),
    )
}