package com.example.btl_android_project.presentation.log_food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.local.entity.Food
import com.example.btl_android_project.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LogFoodViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {
    private val _foods = MutableStateFlow<List<Food>>(emptyList())
    val foods: StateFlow<List<Food>> = _foods

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Temporary hardcoded user ID
    private val userId = 1

    init {
        loadFoods()
        syncFoodsFromFirestore()
    }

    private fun loadFoods() {
        viewModelScope.launch {
            foodRepository.getAllFoodsByUser(userId).collectLatest { foodsList ->
                _foods.value = foodsList
            }
        }
    }

    fun searchFoods(query: String) {
        _searchQuery.value = query

        viewModelScope.launch {
            if (query.isBlank()) {
                loadFoods()
            } else {
                foodRepository.searchFoods(query).collectLatest { results ->
                    _foods.value = results
                }
            }
        }
    }

    fun addFood(food: Food) {
        viewModelScope.launch {
            try {
                val foodWithUserId = food.copy(userId = userId)
                foodRepository.insertFood(foodWithUserId)
            } catch (e: Exception) {
                Timber.e("Error adding food: ${e.message}")
            }
        }
    }

    fun updateFood(food: Food) {
        viewModelScope.launch {
            try {
                foodRepository.updateFood(food)
            } catch (e: Exception) {
                Timber.e("Error updating food: ${e.message}")
            }
        }
    }

    fun deleteFood(food: Food) {
        viewModelScope.launch {
            try {
                foodRepository.deleteFood(food)
            } catch (e: Exception) {
                Timber.e("Error deleting food: ${e.message}")
            }
        }
    }

    suspend fun getFoodById(foodId: Int): Food? {
        return foodRepository.getFoodById(foodId)
    }

    private fun syncFoodsFromFirestore() {
        viewModelScope.launch {
            try {
                foodRepository.syncFoodsFromFirestore(userId)
            } catch (e: Exception) {
                Timber.e("Error syncing foods from Firestore: ${e.message}")
            }
        }
    }
}