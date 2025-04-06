package com.example.btl_android_project.presentation.log_food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.local.entity.Food
import com.example.btl_android_project.local.entity.Nutrition
import com.example.btl_android_project.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CreateFoodNutritionViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {
    // Danh sách mặc định
    private val _nutritions = MutableStateFlow<List<Nutrition>>(
        listOf(
            Nutrition(number = "1", name = "Calories", amount = 0f, unitName = "kcal"),
            Nutrition(number = "2", name = "Protein", amount = 0f, unitName = "g"),
            Nutrition(number = "3", name = "Carbs", amount = 0f, unitName = "g"),
            Nutrition(number = "4", name = "Fat", amount = 0f, unitName = "g")
        )
    )
    val nutritions: StateFlow<List<Nutrition>> = _nutritions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _nutritionLoaded = MutableSharedFlow<Unit>()
    val nutritionLoaded = _nutritionLoaded.asSharedFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved

    // Thông tin từ màn hình trước
    var foodName: String = ""
    var description: String = ""
    var servingsSize: Int = 0
    var servingsUnit: String = ""
    var servingsPerContainer: Int = 0
    private val userId = 1

    // Hàm cập nhật
    fun updateNutrition(index: Int, amount: Float) {
        if (amount < 0) {
            Timber.e("Amount cannot be negative.")
            return
        }

        val updatedList = _nutritions.value.toMutableList()
        val nutrition = updatedList[index]
        updatedList[index] = nutrition.copy(amount = amount)
        _nutritions.value = updatedList
    }

    fun loadNutrition(foodId: Int) {
        viewModelScope.launch {
            val food = foodRepository.getFoodById(foodId)
            food?.let {
                _nutritions.value = food.nutritions
                _nutritionLoaded.emit(Unit)
            }
        }
    }

    // Update
    fun updateFood(foodId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {

                val calories = _nutritions.value.find { it.name == "Calories" }?.amount ?: 0f
                val protein = _nutritions.value.find { it.name == "Protein" }?.amount ?: 0f
                val carbs = _nutritions.value.find { it.name == "Carbs" }?.amount ?: 0f
                val fat = _nutritions.value.find { it.name == "Fat" }?.amount ?: 0f

                // Check for invalid values
                if (calories < 0 || protein < 0 || carbs < 0 || fat < 0) {
                    Timber.e("Invalid nutrition values. Amounts cannot be negative.")
                    _isLoading.value = false
                    return@launch
                }

                val food = Food(
                    id = foodId,
                    name = foodName,
                    description = description,
                    servingsSize = servingsSize,
                    servingsUnit = servingsUnit,
                    servingsPerContainer = servingsPerContainer,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat,
                    nutritions = _nutritions.value,
                    userId = userId
                )

                foodRepository.updateFood(food)
                _isSaved.value = true
            } catch (e: Exception) {
                Timber.e("Error saving food: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Lưu
    fun saveFood() {
        _isLoading.value = true
        viewModelScope.launch {
            try {

                val calories = _nutritions.value.find { it.name == "Calories" }?.amount ?: 0f
                val protein = _nutritions.value.find { it.name == "Protein" }?.amount ?: 0f
                val carbs = _nutritions.value.find { it.name == "Carbs" }?.amount ?: 0f
                val fat = _nutritions.value.find { it.name == "Fat" }?.amount ?: 0f

                // Check for invalid values
                if (calories < 0 || protein < 0 || carbs < 0 || fat < 0) {
                    Timber.e("Invalid nutrition values. Amounts cannot be negative.")
                    _isLoading.value = false
                    return@launch
                }

                val food = Food(
                    name = foodName,
                    description = description,
                    servingsSize = servingsSize,
                    servingsUnit = servingsUnit,
                    servingsPerContainer = servingsPerContainer,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat,
                    nutritions = _nutritions.value,
                    userId = userId
                )

                foodRepository.insertFood(food)
                _isSaved.value = true
            } catch (e: Exception) {
                Timber.e("Error saving food: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

}
