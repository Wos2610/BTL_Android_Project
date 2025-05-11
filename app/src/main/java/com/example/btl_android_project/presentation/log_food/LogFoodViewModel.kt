package com.example.btl_android_project.presentation.log_food

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.auth.FirebaseAuthDataSource
import com.example.btl_android_project.local.entity.DiaryFoodCrossRef
import com.example.btl_android_project.local.entity.Food
import com.example.btl_android_project.local.enums.MealType
import com.example.btl_android_project.repository.DailyDiaryRepository
import com.example.btl_android_project.repository.DiaryFoodCrossRefRepository
import com.example.btl_android_project.repository.FoodRepository
import com.example.btl_android_project.repository.MealFoodCrossRefRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LogFoodViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val mealFoodCrossRefRepository: MealFoodCrossRefRepository,
    private val dailyDiaryRepository: DailyDiaryRepository,
    private val dailyDiaryFoodCrossRefRepository: DiaryFoodCrossRefRepository,
    private val firebaseAuthDataSource: FirebaseAuthDataSource

) : ViewModel() {
    private val _foods = MutableStateFlow<List<Food>>(emptyList())
    val foods: StateFlow<List<Food>> = _foods

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val userId = firebaseAuthDataSource.getCurrentUserId()
    val logDate : LocalDate = LocalDate.now()

    var selectedMealType: String? = null

    fun loadFoods() {
        viewModelScope.launch {
            foodRepository.getAllFoodsByUser(userId.toString()).collectLatest { foodsList ->
                _foods.value = foodsList
            }
        }
    }

    fun addFood(food: Food) {
        viewModelScope.launch {
            try {
                val foodWithUserId = food.copy(userId = userId.toString())
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

    suspend fun getFoodById(foodId: String): Food? {
        return foodRepository.getFoodById(foodId)
    }

    fun syncFoodsFromFirestore() {
        viewModelScope.launch {
            try {
                foodRepository.syncFoodsFromFirestore(userId.toString())
                mealFoodCrossRefRepository.pullFromFireStore(userId.toString())
                dailyDiaryFoodCrossRefRepository.pullFromFireStore(userId.toString())
            } catch (e: Exception) {
                Timber.e("Error syncing foods from Firestore: ${e.message}")
            }
        }
    }

    fun searchFoods(query: String){
        Log.d("LogFoodViewModel", "Searching foods with query: $query")
        viewModelScope.launch {
            val result = foodRepository.searchFoods(query, userId.toString())
            _foods.value = result
        }
    }

    fun addFoodToDiary(
        foodId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val dailyDiary = dailyDiaryRepository.getOrCreateDailyDiary(userId.toString(), logDate)
            val dairyFoodCrossRef = DiaryFoodCrossRef(
                foodId = foodId,
                diaryId = dailyDiary.id,
                userId = userId.toString(),
                servings = 1,
                mealType = MealType.valueOf(selectedMealType ?: ""),
            )
            dailyDiaryFoodCrossRefRepository.insertOrUpdateDiaryFoodCrossRef(dairyFoodCrossRef)

            val food = foodRepository.getFoodById(foodId)
            val updatedDailyDiary = dailyDiary.copy(
                totalFoodCalories = dailyDiary.totalFoodCalories + (food?.calories ?: 0f),
                totalCarbs = dailyDiary.totalCarbs + (food?.carbs ?: 0f),
                totalProtein = dailyDiary.totalProtein + (food?.protein ?: 0f),
                totalFat = dailyDiary.totalFat + (food?.fat ?: 0f),
            )

            dailyDiaryRepository.updateDailyDiary(updatedDailyDiary)
            onSuccess()
        }
    }
}