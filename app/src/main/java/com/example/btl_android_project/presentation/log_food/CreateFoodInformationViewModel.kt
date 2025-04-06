package com.example.btl_android_project.presentation.log_food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.local.entity.Food
import com.example.btl_android_project.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CreateFoodInformationViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {


    private val _foodName = MutableStateFlow("")
    val foodName: StateFlow<String> = _foodName

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _servingsSize = MutableStateFlow(0)
    val servingsSize: StateFlow<Int> = _servingsSize

    private val _servingsUnit = MutableStateFlow("")
    val servingsUnit: StateFlow<String> = _servingsUnit

    private val _servingsPerContainer = MutableStateFlow(0)
    val servingsPerContainer: StateFlow<Int> = _servingsPerContainer

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved

    // Giả sử userId là cố định
    private val userId = 1

    fun setName(name: String) {
        _foodName.value = name
    }

    fun setDescription(description: String) {
        _description.value = description
    }

    fun setServingsSize(size: Int) {
        _servingsSize.value = size
    }

    fun setServingsUnit(unit: String) {
        _servingsUnit.value = unit
    }

    fun setServingsPerContainer(count: Int) {
        _servingsPerContainer.value = count
    }


    // Hàm kiểm tra hợp lệ toàn bộ các trường
    fun validateBasicFoodInfo(): Boolean {
        if (_foodName.value.isBlank()) return false
        if (_description.value.isBlank()) return false
        if (_servingsSize.value <= 0) return false
        if (_servingsUnit.value.isBlank()) return false
        if (_servingsPerContainer.value <= 0) return false
        return true
    }

    // Hàm gọi trước khi chuyển sang màn hình nhập dinh dưỡng
    fun saveBasicFoodInfo(): Boolean {
        return validateBasicFoodInfo()
    }

    // Lưu ý: Sau khi qua màn hình nhập Nutrition, các giá trị dinh dưỡng sẽ được chuyển qua CreateFoodNutritionViewModel
    fun createFood(
        calories: Float,
        protein: Float,
        carbs: Float,
        fat: Float,
        nutritions: List<com.example.btl_android_project.local.entity.Nutrition>
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val food = Food(
                    name = _foodName.value,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat,
                    description = _description.value,
                    servingsSize = _servingsSize.value,
                    servingsUnit = _servingsUnit.value,
                    servingsPerContainer = _servingsPerContainer.value,
                    nutritions = nutritions,
                    userId = userId
                )
                foodRepository.insertFood(food)
                _isSaved.value = true
            } catch (e: Exception) {
                Timber.e("Error creating food: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
