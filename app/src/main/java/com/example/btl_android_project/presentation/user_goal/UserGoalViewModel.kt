package com.example.btl_android_project.presentation.user_goal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.auth.FirebaseAuthDataSource
import com.example.btl_android_project.local.entity.UserProfile
import com.example.btl_android_project.repository.DailyDiaryRepository
import com.example.btl_android_project.repository.FoodRepository
import com.example.btl_android_project.repository.MealRepository
import com.example.btl_android_project.repository.RecipeRepository
import com.example.btl_android_project.repository.StaticFoodsRepository
import com.example.btl_android_project.repository.StaticRecipeIngredientRepository
import com.example.btl_android_project.repository.StaticRecipesRepository
import com.example.btl_android_project.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserGoalViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val authDataSource: FirebaseAuthDataSource,
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val staticRecipeIngredientRepository: StaticRecipeIngredientRepository,
    private val staticRecipesRepository: StaticRecipesRepository,
    private val staticFoodRepository: StaticFoodsRepository,
    private val foodRepository: FoodRepository,
    private val recipeRepository: RecipeRepository,
    private val mealRepository: MealRepository,
    private val dailyDiaryRepository: DailyDiaryRepository,
) : ViewModel() {
    var userProfileArgument: UserProfileArgument? = null
    var _userWeightGoal: MutableStateFlow<Int> = MutableStateFlow(0)
    val userWeightGoal: StateFlow<Int> = _userWeightGoal.asStateFlow()

    var _userPlanInfo: MutableStateFlow<String> = MutableStateFlow("")
    val userPlanInfo: StateFlow<String> = _userPlanInfo.asStateFlow()
    fun calculateGoal(
    ) {
        viewModelScope.launch {
            userProfileArgument?.let {
                userProfileArgument = userProfileRepository.calculate(it)
                _userWeightGoal.value = userProfileArgument?.caloriesGoal ?: 0
                _userPlanInfo.value = userProfileRepository.checkSuitableWeightGoal(it)
            }
        }
    }

    fun saveUserProfile(
        onSuccess: () -> Unit,
        onLoading: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val userId = authDataSource.getCurrentUserId() ?: return@launch
            val userProfile = UserProfile(
                userId = userId.toString(),
                height = userProfileArgument?.heightCm ?: 0f,
                currentWeight = userProfileArgument?.currentWeight ?: 0f,
                initialWeight = userProfileArgument?.currentWeight ?: 0f,
                weightGoal = userProfileArgument?.goalWeight ?: 0f,
                calorieGoal = userWeightGoal.value,
                waterGoal = userProfileArgument?.goalWater?.toInt() ?: 0
            )
            userProfileRepository.insertUserProfile(userProfile)
            loadUserData(
                onSuccess = onSuccess,
                onLoading = onLoading,
            )
        }
    }

    suspend fun loadUserData(
        onSuccess: () -> Unit,
        onLoading: (Boolean) -> Unit
    ) {
        onLoading(true)
        try {
            val currentUserId = firebaseAuthDataSource.getCurrentUserId().toString()
            staticFoodRepository.pullFromFireStore()
            staticRecipeIngredientRepository.pullStaticRecipeIngredientsFromFireStore()
            staticRecipesRepository.pullStaticRecipesFromFireStore()
            foodRepository.syncFoodsFromFirestore(userId = currentUserId)
            recipeRepository.pullFromFireStore(userId = currentUserId)
            mealRepository.pullFromFireStoreByUserId(userId = currentUserId)
            dailyDiaryRepository.pullFromFireStoreByUserId(userId = currentUserId)
            onLoading(false)
            onSuccess()
        } catch (e: Exception) {
            Log.e("UserGoalViewModel", "Error loading user data", e)
        } finally {
            onLoading(false)
        }
    }
}