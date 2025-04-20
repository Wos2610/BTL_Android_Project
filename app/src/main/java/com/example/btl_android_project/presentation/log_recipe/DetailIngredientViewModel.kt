package com.example.btl_android_project.presentation.log_recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.local.entity.RecipeIngredient
import com.example.btl_android_project.repository.StaticRecipeIngredientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailIngredientViewModel @Inject constructor(
    private val staticRecipeIngredientRepository: StaticRecipeIngredientRepository
): ViewModel() {
    var ingredient: MutableStateFlow<RecipeIngredient?> = MutableStateFlow(null)

    fun setIngredient(ingredient: RecipeIngredient) {
        this.ingredient.value = ingredient
    }

    fun setIngredientById(id: Int){
        viewModelScope.launch {
            val staticRecipeIngredient = staticRecipeIngredientRepository.getIngredientById(id)
            val recipeIngredient = RecipeIngredient(
                id = staticRecipeIngredient?.id ?: 0,
                fdcId = staticRecipeIngredient?.fdcId ?: 0,
                description = staticRecipeIngredient?.description ?: "",
                foodNutrients = staticRecipeIngredient?.foodNutrients ?: emptyList(),
                numberOfServings = 1,
                servingSize = "100g",
            )
            ingredient.value = recipeIngredient
        }
    }
}