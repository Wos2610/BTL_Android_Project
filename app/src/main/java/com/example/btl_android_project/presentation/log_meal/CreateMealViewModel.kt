package com.example.btl_android_project.presentation.log_meal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.local.entity.Meal
import com.example.btl_android_project.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

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

    var currentMealId: Int = 0
    var _meal : MutableStateFlow<Meal?> = MutableStateFlow(null)
    val meal = _meal.asStateFlow()

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
        val total = _mealItems.value.sumOf { if(it is MealItem.FoodItem) it.food.calories.toInt() * it.food.servings else if (it is MealItem.RecipeItem) it.recipe.calories.toInt() * it.recipe.servings else 0 }
        _totalCalories.value = total

        val totalCarbs = _mealItems.value.sumOf { if(it is MealItem.FoodItem) it.food.carbs.toInt() * it.food.servings else if (it is MealItem.RecipeItem) it.recipe.carbs.toInt() * it.recipe.servings else 0 }
        _totalCarbs.value = totalCarbs

        val totalProtein = _mealItems.value.sumOf { if(it is MealItem.FoodItem) it.food.protein.toInt() * it.food.servings else if (it is MealItem.RecipeItem) it.recipe.protein.toInt() * it.recipe.servings else 0 }
        _totalProtein.value = totalProtein

        val totalFat = _mealItems.value.sumOf { if(it is MealItem.FoodItem) it.food.fat.toInt() * it.food.servings else if (it is MealItem.RecipeItem) it.recipe.fat.toInt() * it.recipe.servings else 0 }
        _totalFat.value = totalFat

        val sum = totalCarbs + totalProtein + totalFat
        if (sum == 0) return
        carbsAmount = ((totalCarbs.toDouble() / sum) * 100).roundToInt()
        proteinAmount = ((totalProtein.toDouble() / sum) * 100).roundToInt()
        fatAmount = ((totalFat.toDouble() / sum) * 100).roundToInt()
    }

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
                selectedRecipeItems = mergedRecipeItems,
                totalCalories = _totalCalories.value,
                totalCarbs = _totalCarbs.value,
                totalProtein = _totalProtein.value,
                totalFat = _totalFat.value,
            )
            onSaveSuccess()
        }
    }
}