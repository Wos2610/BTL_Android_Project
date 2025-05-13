package com.example.btl_android_project.presentation.log_food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.local.entity.Food
import com.example.btl_android_project.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateFoodInformationViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _food = MutableStateFlow<Food?>(null)
    val food: StateFlow<Food?> get() = _food

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


    fun validateBasicFoodInfo(): Boolean {
        if (_foodName.value.isBlank()) return false
        if (_description.value.isBlank()) return false
        if (_servingsSize.value <= 0) return false
        if (_servingsUnit.value.isBlank()) return false
        if (_servingsPerContainer.value <= 0) return false
        return true
    }

    fun saveBasicFoodInfo(): Boolean {
        return validateBasicFoodInfo()
    }

    fun loadFood(foodId: String) {
        viewModelScope.launch {
            _food.value = foodRepository.getFoodById(foodId)
        }
    }
}
