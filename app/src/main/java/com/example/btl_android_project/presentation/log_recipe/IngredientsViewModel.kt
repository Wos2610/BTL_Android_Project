package com.example.btl_android_project.presentation.log_recipe

import androidx.lifecycle.ViewModel
import com.example.btl_android_project.local.entity.RecipeIngredient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class IngredientsViewModel @Inject constructor(): ViewModel() {
    var recipeName: String = ""
    var servings: Int = 0
    private val _ingredients = MutableStateFlow<List<RecipeIngredient>>(emptyList())
    val ingredients = _ingredients
        .asStateFlow()

    private val _totalCalories = MutableStateFlow(0)
    val totalCalories = _totalCalories
        .asStateFlow()


    fun setIngredients(ingredients: List<RecipeIngredient>) {
        _ingredients.value = ingredients
    }

    fun addIngredient(ingredient: RecipeIngredient) {
        _ingredients.value = _ingredients.value + ingredient
    }

    fun removeIngredient(ingredient: RecipeIngredient) {
        _ingredients.value = _ingredients.value - ingredient
    }

    fun clearIngredients() {
        _ingredients.value = emptyList()
    }


    fun calculateTotalCalories() {
        val total = _ingredients.value.sumOf { it.foodNutrients.firstOrNull{ it.name.contains("Energy") }?.amount?.toInt() ?: 0 }
        _totalCalories.value = total
    }
}