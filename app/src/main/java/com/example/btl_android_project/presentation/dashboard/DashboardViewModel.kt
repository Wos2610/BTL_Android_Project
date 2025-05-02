package com.example.btl_android_project.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.auth.FirebaseAuthDataSource
import com.example.btl_android_project.repository.DailyDiaryRepository
import com.example.btl_android_project.repository.DiaryFoodCrossRefRepository
import com.example.btl_android_project.repository.FoodRepository
import com.example.btl_android_project.repository.MealFoodCrossRefRepository
import com.example.btl_android_project.repository.MealRecipeCrossRefRepository
import com.example.btl_android_project.repository.MealRepository
import com.example.btl_android_project.repository.RecipeRepository
import com.example.btl_android_project.repository.StaticFoodsRepository
import com.example.btl_android_project.repository.StaticRecipeIngredientRepository
import com.example.btl_android_project.repository.StaticRecipesRepository
import com.example.btl_android_project.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val staticRecipeIngredientRepository: StaticRecipeIngredientRepository,
    private val staticRecipesRepository: StaticRecipesRepository,
    private val staticFoodRepository: StaticFoodsRepository,
    private val mealFoodCrossRefRepository: MealFoodCrossRefRepository,
    private val mealRecipeCrossRefRepository: MealRecipeCrossRefRepository,
    private val foodRepository: FoodRepository,
    private val recipeRepository: RecipeRepository,
    private val mealRepository: MealRepository,
    private val dailyDiaryRepository: DailyDiaryRepository,
    private val diaryFoodCrossRefRepository: DiaryFoodCrossRefRepository,
    private val diaryRecipeCrossRefRepository: DiaryFoodCrossRefRepository,
    private val diaryMealCrossRefRepository: DiaryFoodCrossRefRepository,
    private val firebaseAuthDataSource: FirebaseAuthDataSource

) : ViewModel() {

    val currentUserId = firebaseAuthDataSource.getCurrentUserId().toString()

    fun pullStaticRecipeIngredients() {
        viewModelScope.launch {
            staticRecipeIngredientRepository.pullStaticRecipeIngredients()
        }
    }

    fun pullStaticRecipes() {
        viewModelScope.launch {
            staticRecipesRepository.pullStaticRecipes()
        }
    }

    fun pullStaticFoods() {
        viewModelScope.launch {
            staticFoodRepository.pullStaticFoods()
        }
    }

    fun pushStaticFoodsToFireStore() {
        viewModelScope.launch {
            staticFoodRepository.pushToFireStore()
        }
    }

    fun loadDataFromFireStore() {
        viewModelScope.launch {
            staticFoodRepository.pullFromFireStore()
            staticRecipeIngredientRepository.pullStaticRecipeIngredientsFromFireStore()
            staticRecipesRepository.pullStaticRecipesFromFireStore()
            foodRepository.syncFoodsFromFirestore(userId = currentUserId)
            recipeRepository.pullFromFireStore(userId = currentUserId)
            mealRepository.pullFromFireStoreByUserId(userId = currentUserId)
            dailyDiaryRepository.pullFromFireStoreByUserId(userId = currentUserId)
        }
    }


}