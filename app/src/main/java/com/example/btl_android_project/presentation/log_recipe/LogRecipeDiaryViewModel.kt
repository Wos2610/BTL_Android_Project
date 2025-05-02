package com.example.btl_android_project.presentation.log_recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.local.entity.Recipe
import com.example.btl_android_project.repository.MealRepository
import com.example.btl_android_project.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class LogRecipeDiaryViewModel @Inject constructor(
    val recipeRepository: RecipeRepository,
    val mealRepository: MealRepository
): ViewModel() {
    var servings: Int = 1

    var currentRecipeId: String = ""

    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe = _recipe
        .asStateFlow()

    private val _totalCalories = MutableStateFlow(0)
    val totalCalories = _totalCalories
        .asStateFlow()
    private val _totalCarbs = MutableStateFlow(0)
    val totalCarbs = _totalCarbs
        .asStateFlow()
    private val _totalProtein = MutableStateFlow(0)
    val totalProteinFlow = _totalProtein
        .asStateFlow()
    private val _totalFat = MutableStateFlow(0)
    val totalFatFlow = _totalFat
        .asStateFlow()

    var carbsAmount: Int = 0
    var proteinAmount: Int = 0
    var fatAmount: Int = 0


    fun calculateTotalNutrition() {
        val total = recipe.value?.ingredients?.sumOf { (it.foodNutrients.firstOrNull{ it.name.contains("Energy") }?.amount?.toInt() ?: 0) * it.numberOfServings } ?: 0
        _totalCalories.value = total

        val totalCarbs = recipe.value?.ingredients?.sumOf { (it.foodNutrients.firstOrNull{ it.name.contains("Carbohydrate") }?.amount?.toInt() ?: 0) * it.numberOfServings } ?: 0
        _totalCarbs.value = totalCarbs

        val totalProtein = recipe.value?.ingredients?.sumOf { (it.foodNutrients.firstOrNull{ it.name.contains("Protein") }?.amount?.toInt() ?: 0) * it.numberOfServings } ?: 0
        _totalProtein.value = totalProtein

        val totalFat = recipe.value?.ingredients?.sumOf { (it.foodNutrients.firstOrNull{ it.name.contains("fat") }?.amount?.toInt() ?: 0) * it.numberOfServings } ?: 0
        _totalFat.value = totalFat

        val sum = totalCarbs + totalProtein + totalFat
        if (sum == 0) return
        carbsAmount = ((totalCarbs.toDouble() / sum) * 100).roundToInt()
        proteinAmount = ((totalProtein.toDouble() / sum) * 100).roundToInt()
        fatAmount = ((totalFat.toDouble() / sum) * 100).roundToInt()
    }

    fun setRecipe(recipe: Recipe) {
        _recipe.value = recipe
    }

    fun getRecipeById(
        recipeId: String,
    ) {
        viewModelScope.launch {
            val recipe = recipeRepository.getRecipeById(recipeId)
            if (recipe != null) {
                currentRecipeId = recipeId
                setRecipe(recipe)
                calculateTotalNutrition()
            }
        }
    }

    fun sendRecipe() : Recipe? {
        val recipe = recipe.value
        return recipe?.copy(servings = servings)
    }

}