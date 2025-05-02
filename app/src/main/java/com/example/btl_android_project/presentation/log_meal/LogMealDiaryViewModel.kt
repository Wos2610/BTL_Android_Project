package com.example.btl_android_project.presentation.log_meal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.local.entity.Meal
import com.example.btl_android_project.repository.MealFoodCrossRefRepository
import com.example.btl_android_project.repository.MealRecipeCrossRefRepository
import com.example.btl_android_project.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogMealDiaryViewModel @Inject constructor(
    val mealRepository: MealRepository,
    val mealFoodCrossRefRepository: MealFoodCrossRefRepository,
    val mealRecipeCrossRefRepository: MealRecipeCrossRefRepository,
) : ViewModel() {
    var currentMealId: String = ""
    var _meal : MutableStateFlow<Meal?> = MutableStateFlow(null)
    val meal = _meal.asStateFlow()

    val _mealItems: MutableStateFlow<List<MealItem>> = MutableStateFlow(emptyList())
    val mealItems: StateFlow<List<MealItem>> = _mealItems.asStateFlow()

    fun initMeal(
        name: String,
        mealType: String,
        userId: Int,
    ) {
        viewModelScope.launch {
//            mealId = mealRepository.initMeal(
//                name = name,
//                mealType = mealType,
//                userId = userId,
//            )
        }
    }

    fun addMealItem(mealItem: MealItem) {
        _mealItems.value = _mealItems.value + mealItem
    }

    fun removeMealItem(mealItem: MealItem) {
        _mealItems.value = _mealItems.value - mealItem
    }

    fun clearMealItems() {
        _mealItems.value = emptyList()
    }

    fun saveMeal(
        onSaveSuccess: () -> Unit
    ) {
//        viewModelScope.launch {
//            mealRepository.LogMealDiary(
//                name = mealName,
//                mealType = mealType,
//                userId = userId,
//                selectedFoodItems = _mealItems.value.filterIsInstance<MealItem.FoodItem>(),
//                selectedRecipeItems = _mealItems.value.filterIsInstance<MealItem.RecipeItem>()
//            )
//            onSaveSuccess()
//        }
    }

    fun getMealById(mealId: String) {
        viewModelScope.launch {
            val mealWithFoodsAndRecipes = mealRepository.getMealWithFoodsAndRecipes(mealId)
            _meal.value = mealWithFoodsAndRecipes.meal
            val foodItems = mealWithFoodsAndRecipes.foods.map { MealItem.FoodItem(it) }
            val recipeItems = mealWithFoodsAndRecipes.recipes.map { MealItem.RecipeItem(it) }
            _mealItems.value = foodItems + recipeItems
            currentMealId = mealId
        }
    }

}