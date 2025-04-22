package com.example.btl_android_project.presentation.log_food
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.local.entity.Food
import com.example.btl_android_project.repository.MealRepository
import com.example.btl_android_project.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class LogFoodDiaryViewModel @Inject constructor(
    val foodRepository: FoodRepository,
    val mealRepository: MealRepository
): ViewModel() {
    var servings: Int = 1

    var currentFoodId: Int = 0

    private val _food = MutableStateFlow<Food?>(null)
    val food = _food
        .asStateFlow()

    var carbsAmount: Int = 0
    var proteinAmount: Int = 0
    var fatAmount: Int = 0


    fun calculateTotalNutrition() {
        val totalCarbs = food.value?.carbs ?: 0f

        val totalProtein = food.value?.protein ?: 0f
        val totalFat = food.value?.fat ?: 0f

        val sum = totalCarbs + totalProtein + totalFat
        if (sum == 0f) return
        carbsAmount = ((totalCarbs.toDouble() / sum) * 100).roundToInt()
        proteinAmount = ((totalProtein.toDouble() / sum) * 100).roundToInt()
        fatAmount = ((totalFat.toDouble() / sum) * 100).roundToInt()
    }

    fun setFood(food: Food) {
        _food.value = food
    }

    fun getFoodById(
        foodId: Int,
    ) {
        viewModelScope.launch {
            val food = foodRepository.getFoodByFoodId(foodId)
            if (food != null) {
                currentFoodId = foodId
                calculateTotalNutrition()
                setFood(food)
            }
        }
    }

    fun sendFood() : Food? {
        val food = food.value
        return food?.copy(servings = servings)
    }

}