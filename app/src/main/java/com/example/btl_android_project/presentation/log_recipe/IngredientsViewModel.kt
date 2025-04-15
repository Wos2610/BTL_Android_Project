package com.example.btl_android_project.presentation.log_recipe

import androidx.lifecycle.ViewModel
import com.example.btl_android_project.local.entity.StaticRecipeIngredient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class IngredientsViewModel @Inject constructor(): ViewModel() {
    private val _ingredients = MutableStateFlow<List<StaticRecipeIngredient>>(emptyList())
    val ingredients = _ingredients
        .asStateFlow()

    private val _totalCalories = MutableStateFlow(0)
    val totalCalories = _totalCalories
        .asStateFlow()


    fun setIngredients(ingredients: List<StaticRecipeIngredient>) {
        _ingredients.value = ingredients
    }

    fun addIngredient(ingredient: StaticRecipeIngredient) {
        _ingredients.value = _ingredients.value + ingredient
    }

    fun removeIngredient(ingredient: StaticRecipeIngredient) {
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