package com.example.btl_android_project.presentation.sign_in

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.auth.FirebaseAuthDataSource
import com.example.btl_android_project.repository.DailyDiaryRepository
import com.example.btl_android_project.repository.DiaryFoodCrossRefRepository
import com.example.btl_android_project.repository.DiaryMealCrossRefRepository
import com.example.btl_android_project.repository.DiaryRecipeCrossRefRepository
import com.example.btl_android_project.repository.ExercisesRepository
import com.example.btl_android_project.repository.FoodRepository
import com.example.btl_android_project.repository.LogWaterRepository
import com.example.btl_android_project.repository.LogWeightRepository
import com.example.btl_android_project.repository.MealFoodCrossRefRepository
import com.example.btl_android_project.repository.MealRecipeCrossRefRepository
import com.example.btl_android_project.repository.MealRepository
import com.example.btl_android_project.repository.RecipeRepository
import com.example.btl_android_project.repository.StaticFoodsRepository
import com.example.btl_android_project.repository.StaticRecipeIngredientRepository
import com.example.btl_android_project.repository.StaticRecipesRepository
import com.example.btl_android_project.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val staticRecipeIngredientRepository: StaticRecipeIngredientRepository,
    private val staticRecipesRepository: StaticRecipesRepository,
    private val staticFoodRepository: StaticFoodsRepository,
    private val exerciseRepository: ExercisesRepository,
    private val foodRepository: FoodRepository,
    private val logWeightRepository: LogWeightRepository,
    private val logWaterRepository: LogWaterRepository,
    private val recipeRepository: RecipeRepository,
    private val mealRepository: MealRepository,
    private val dailyDiaryRepository: DailyDiaryRepository,
    private val userProfileRepository: UserProfileRepository,
    private val mealFoodCrossRefRepository: MealFoodCrossRefRepository,
    private val mealRecipeCrossRefRepository: MealRecipeCrossRefRepository,
    private val diaryFoodCrossRefRepository: DiaryFoodCrossRefRepository,
    private val diaryRecipeCrossRefRepository: DiaryRecipeCrossRefRepository,
    private val diaryMealCrossRefRepository: DiaryMealCrossRefRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<SignInUiState>(SignInUiState.Idle)
    val uiState: StateFlow<SignInUiState> = _uiState

    fun loginUser(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _uiState.value = SignInUiState.Error(Exception("Email or password cannot be empty"))
            return
        }

        _uiState.value = SignInUiState.Loading
        viewModelScope.launch {
            try {
                firebaseAuthDataSource.loginUserSuspend(email, password)
                checkAndLoadUserData()
            } catch (e: Exception) {
                _uiState.value = SignInUiState.Error(e)
            }
        }
    }

    fun checkUserLoggedIn() {
        _uiState.value = SignInUiState.Loading
        viewModelScope.launch {
            try {
                val isLoggedIn = firebaseAuthDataSource.checkUserLoggedInSuspend()
                if (isLoggedIn) {
                    checkAndLoadUserData()
                } else {
                    _uiState.value = SignInUiState.NotLoggedIn
                }
            } catch (e: Exception) {
                _uiState.value = SignInUiState.Error(e)
            }
        }
    }

    private suspend fun checkAndLoadUserData() {
        try {
            val currentUserId = firebaseAuthDataSource.getCurrentUserId().toString()
            val userProfile = userProfileRepository.getUserProfileByUserId(currentUserId)

            if (userProfile != null) {
                loadUserData()
                _uiState.value = SignInUiState.Success
            } else {
                _uiState.value = SignInUiState.NeedsSetup
            }
        } catch (e: Exception) {
            Log.e("SignInViewModel", "Error checking user set up goal", e)
            _uiState.value = SignInUiState.Error(e)
        }
    }

    private suspend fun loadUserData() {
        try {
            val currentUserId = firebaseAuthDataSource.getCurrentUserId().toString()
            coroutineScope {
                launch { staticFoodRepository.pullFromFireStore() }
                launch { staticRecipeIngredientRepository.pullStaticRecipeIngredientsFromFireStore() }
                launch { staticRecipesRepository.pullStaticRecipesFromFireStore() }
                launch { exerciseRepository.syncExercisesFromFirestore(userId = currentUserId) }
                launch { foodRepository.syncFoodsFromFirestore(userId = currentUserId) }
                launch { logWaterRepository.syncLogsFromFirestore(userId = currentUserId) }
                launch { logWeightRepository.syncLogWeightsFromFirestore(userId = currentUserId) }
                launch { recipeRepository.pullFromFireStore(userId = currentUserId) }
                launch {
                    mealRepository.pullFromFireStoreByUserId(userId = currentUserId)
                    dailyDiaryRepository.pullFromFireStoreByUserId(userId = currentUserId)
                }
            }
        } catch (e: Exception) {
            Log.e("SignInViewModel", "Error loading user data", e)
            throw e
        }
    }
}
