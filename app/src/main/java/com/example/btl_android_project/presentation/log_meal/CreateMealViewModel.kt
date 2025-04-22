package com.example.btl_android_project.presentation.log_meal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateMealViewModel @Inject constructor(
    val mealRepository: MealRepository,
) : ViewModel() {
    var mealId: Int = 0
    var mealName: String = ""
    var mealType: String = ""
    var userId: Int = 0

    val _mealItems: MutableStateFlow<List<MealItem>> = MutableStateFlow(emptyList())
    val mealItems: StateFlow<List<MealItem>> = _mealItems.asStateFlow()

    fun initMeal(
        name: String,
        mealType: String,
        userId: Int,
    ) {
        viewModelScope.launch {
            mealId = mealRepository.initMeal(
                name = name,
                mealType = mealType,
                userId = userId,
            )
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
        viewModelScope.launch {
            val rawFoodItems = _mealItems.value
                .filterIsInstance<MealItem.FoodItem>()

            val mergedFoodItems = rawFoodItems
                .groupBy { it.food.id }
                .map { (_, duplicates) ->
                    // Lấy một Food mẫu rồi copy với servings = tổng servings
                    val any = duplicates.first().food
                    val totalServings = duplicates.sumOf { it.food.servings }
                    MealItem.FoodItem(
                        food = any.copy(servings = totalServings)
                    )
                }

            val rawRecipeItems = _mealItems.value
                .filterIsInstance<MealItem.RecipeItem>()

            val mergedRecipeItems = rawRecipeItems
                .groupBy { it.recipe.id }
                .map { (_, duplicates) ->
                    val any = duplicates.first().recipe
                    val totalServings = duplicates.sumOf { it.recipe.servings }
                    MealItem.RecipeItem(
                        recipe = any.copy(servings = totalServings)
                    )
                }

            mealRepository.createMeal(
                name = mealName,
                mealType = mealType,
                userId = userId,
                selectedFoodItems = mergedFoodItems,
                selectedRecipeItems = mergedRecipeItems
            )
            onSaveSuccess()
        }
    }
}