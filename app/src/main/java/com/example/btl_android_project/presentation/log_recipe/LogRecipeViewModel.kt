package com.example.btl_android_project.presentation.log_recipe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.auth.FirebaseAuthDataSource
import com.example.btl_android_project.local.MealType
import com.example.btl_android_project.local.entity.DiaryFoodCrossRef
import com.example.btl_android_project.local.entity.DiaryRecipeCrossRef
import com.example.btl_android_project.local.entity.Recipe
import com.example.btl_android_project.repository.DailyDiaryRepository
import com.example.btl_android_project.repository.DiaryRecipeCrossRefRepository
import com.example.btl_android_project.repository.MealRecipeCrossRefRepository
import com.example.btl_android_project.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LogRecipeViewModel @Inject constructor(
    val recipeRepository: RecipeRepository,
    val mealRecipeCrossRefRepository: MealRecipeCrossRefRepository,
    val dailyDiaryRepository: DailyDiaryRepository,
    val dailyDiaryRecipeCrossRefRepository: DiaryRecipeCrossRefRepository,
    val firebaseAuthDataSource: FirebaseAuthDataSource,
): ViewModel() {
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes = _recipes.asStateFlow()
    var userId: String = firebaseAuthDataSource.getCurrentUserId().toString()
    val logDate : LocalDate = LocalDate.now()
    var selectedMealType: String? = null

    fun pullRecipesFromFireStore() {
        viewModelScope.launch {
            recipeRepository.pullFromFireStore(userId = userId)
            mealRecipeCrossRefRepository.pullFromFireStore(userId = userId)
        }
    }

    fun loadRecipes() {
        viewModelScope.launch {
            recipeRepository.getRecipesByUserId(userId).collect { allRecipes ->
                _recipes.value = allRecipes
            }
        }
    }

    fun searchRecipes(query: String) {
        Log.d("LogRecipeViewModel", "Searching recipes with query: $query")
        viewModelScope.launch {
            val results = recipeRepository.searchRecipes(query, userId)
            _recipes.value = results
        }
    }

    fun addRecipeToDiary(
        recipeId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val dailyDiary = dailyDiaryRepository.getOrCreateDailyDiary(userId, logDate)
            val dairyRecipeCrossRef = DiaryRecipeCrossRef(
                recipeId = recipeId,
                diaryId = dailyDiary.id,
                userId = userId,
                servings = 1,
                mealType = MealType.valueOf(selectedMealType ?: "")
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