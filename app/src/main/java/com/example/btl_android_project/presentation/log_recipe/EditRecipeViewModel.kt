package com.example.btl_android_project.presentation.log_recipe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.auth.FirebaseAuthDataSource
import com.example.btl_android_project.local.entity.DiaryRecipeCrossRef
import com.example.btl_android_project.local.entity.Recipe
import com.example.btl_android_project.local.entity.RecipeIngredient
import com.example.btl_android_project.local.enums.MealType
import com.example.btl_android_project.repository.DailyDiaryRepository
import com.example.btl_android_project.repository.DiaryRecipeCrossRefRepository
import com.example.btl_android_project.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class EditRecipeViewModel @Inject constructor(
    val recipeRepository: RecipeRepository,
    val firebaseAuthDataSource: FirebaseAuthDataSource,
    val dailyDiaryRepository: DailyDiaryRepository,
    val dailyDiaryRecipeCrossRefRepository: DiaryRecipeCrossRefRepository,
): ViewModel() {
    var recipeName: String = ""
    var servings: Int = 1
    var recipeId: String = ""
    var userId: String = firebaseAuthDataSource.getCurrentUserId().toString()

    var logDate = LocalDate.now()
    var recipe: Recipe? = null

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

    private val _ingredients = MutableStateFlow<List<RecipeIngredient>>(emptyList())

    val ingredients = _ingredients
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

    fun calculateTotalNutrition() {
        val total = _ingredients.value.sumOf { (it.foodNutrients.firstOrNull{ it.name.contains("Energy") }?.amount?.toInt() ?: 0) * it.numberOfServings }
        _totalCalories.value = total

        val totalCarbs = _ingredients.value.sumOf { (it.foodNutrients.firstOrNull{ it.name.contains("Carbohydrate") }?.amount?.toInt() ?: 0) * it.numberOfServings }
        _totalCarbs.value = totalCarbs

        val totalProtein = _ingredients.value.sumOf { (it.foodNutrients.firstOrNull{ it.name.contains("Protein") }?.amount?.toInt() ?: 0) * it.numberOfServings }
        _totalProtein.value = totalProtein

        val totalFat = _ingredients.value.sumOf { (it.foodNutrients.firstOrNull{ it.name.contains("fat") }?.amount?.toInt() ?: 0) * it.numberOfServings }
        _totalFat.value = totalFat

        val sum = totalCarbs + totalProtein + totalFat
        if (sum == 0) return
        carbsAmount = ((totalCarbs.toDouble() / sum) * 100).roundToInt()
        proteinAmount = ((totalProtein.toDouble() / sum) * 100).roundToInt()
        fatAmount = ((totalFat.toDouble() / sum) * 100).roundToInt()
    }

    fun updateRecipe(
        navigateToLogAllFragment: () -> Unit
    ) {
        viewModelScope.launch {
            Log.d("DetailRecipeViewModel", "insertOrUpdateRecipe: $recipeId, $recipeName, $servings, ${_ingredients.value}, ${_totalCalories.value}")
            val newRecipe = Recipe(
                id = recipeId,
                name = recipeName,
                servings = servings,
                ingredients = _ingredients.value,
                calories = _totalCalories.value,
                carbs = _totalCarbs.value,
                fat = _totalFat.value,
                protein = _totalProtein.value,
                userId = userId,
            )
            recipeRepository.updateRecipe(newRecipe)
            navigateToLogAllFragment()
        }
    }

    fun deleteRecipe(
        recipeId: String,
        navigateToLogAllFragment: () -> Unit
    ) {
        viewModelScope.launch {
            recipeRepository.deleteRecipe(recipeId)
            navigateToLogAllFragment()
        }
    }

    fun getRecipeById(
        recipeId: String
    ) {
        viewModelScope.launch {
            val recipe = recipeRepository.getRecipeById(recipeId)
            if (recipe != null) {
//                setRecipe(recipe)
            }
        }
    }

    fun addRecipeToDiary(
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val dailyDiary = dailyDiaryRepository.getOrCreateDailyDiary(userId, logDate)
            val dairyRecipeCrossRef = DiaryRecipeCrossRef(
                recipeId = recipeId,
                diaryId = dailyDiary.id,
                userId = userId,
                servings = 1,
                mealType = MealType.BREAKFAST,
            )
            dailyDiaryRecipeCrossRefRepository.insertDiaryRecipeCrossRef(dairyRecipeCrossRef)

            val recipe = recipeRepository.getRecipeById(recipeId)
            val updatedDailyDiary = dailyDiary.copy(
                totalFoodCalories = dailyDiary.totalFoodCalories + (recipe?.calories?.toFloat() ?: 0f),
                totalCarbs = dailyDiary.totalCarbs + (recipe?.carbs?.toFloat() ?: 0f),
                totalProtein = dailyDiary.totalProtein + (recipe?.protein?.toFloat() ?: 0f),
                totalFat = dailyDiary.totalFat + (recipe?.fat?.toFloat() ?: 0f),
            )

            dailyDiaryRepository.updateDailyDiary(updatedDailyDiary)
            onSuccess()
        }
    }
}