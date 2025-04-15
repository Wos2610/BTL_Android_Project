package com.example.btl_android_project.presentation.log_recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.local.entity.StaticRecipeIngredient
import com.example.btl_android_project.repository.StaticRecipeIngredientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailIngredientViewModel @Inject constructor(
    private val staticRecipeIngredientRepository: StaticRecipeIngredientRepository
): ViewModel() {
    var ingredient: MutableStateFlow<StaticRecipeIngredient?> = MutableStateFlow(null)

    fun setIngredient(ingredient: StaticRecipeIngredient) {
        this.ingredient.value = ingredient
    }

    fun setIngredientById(id: Int){
        viewModelScope.launch {
            val recipeIngredient = staticRecipeIngredientRepository.getIngredientById(id)
            ingredient.value = recipeIngredient
        }
    }
}