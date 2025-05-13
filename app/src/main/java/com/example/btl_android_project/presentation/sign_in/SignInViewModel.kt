package com.example.btl_android_project.presentation.sign_in

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.auth.FirebaseAuthDataSource
import com.example.btl_android_project.local.FirebaseDataManager
import com.example.btl_android_project.repository.DailyDiaryRepository
import com.example.btl_android_project.repository.ExercisesRepository
import com.example.btl_android_project.repository.FoodRepository
import com.example.btl_android_project.repository.LogWaterRepository
import com.example.btl_android_project.repository.LogWeightRepository
import com.example.btl_android_project.repository.MealRepository
import com.example.btl_android_project.repository.RecipeRepository
import com.example.btl_android_project.repository.StaticFoodsRepository
import com.example.btl_android_project.repository.StaticRecipeIngredientRepository
import com.example.btl_android_project.repository.StaticRecipesRepository
import com.example.btl_android_project.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private val firebaseDataManager: FirebaseDataManager,
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
                Log.d("SignInViewModel", "Logging in user with email: $email")
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
            Log.d("SignInViewModel", "Checking if user is logged in")
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
            Log.d("SignInViewModel", "Checking user set up goal")
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
            loadStaticDataIfNeeded()
            withContext(Dispatchers.IO) {
                listOf(
                    async {
                        val startTime = System.currentTimeMillis()
                        val result = safeExecute { exerciseRepository.syncExercisesFromFirestore(userId = currentUserId) }
                        val endTime = System.currentTimeMillis()
                        Log.d("SignInViewModel", "Thời gian đồng bộ exercises: ${endTime - startTime}ms")
                        result
                    },
                    async {
                        val startTime = System.currentTimeMillis()
                        val result = safeExecute { foodRepository.syncFoodsFromFirestore(userId = currentUserId) }
                        val endTime = System.currentTimeMillis()
                        Log.d("SignInViewModel", "Thời gian đồng bộ foods: ${endTime - startTime}ms")
                        result
                    },
                    async {
                        val startTime = System.currentTimeMillis()
                        val result = safeExecute { logWaterRepository.syncLogsFromFirestore(userId = currentUserId) }
                        val endTime = System.currentTimeMillis()
                        Log.d("SignInViewModel", "Thời gian đồng bộ water logs: ${endTime - startTime}ms")
                        result
                    },
                    async {
                        val startTime = System.currentTimeMillis()
                        val result = safeExecute { logWeightRepository.syncLogWeightsFromFirestore(userId = currentUserId) }
                        val endTime = System.currentTimeMillis()
                        Log.d("SignInViewModel", "Thời gian đồng bộ weight logs: ${endTime - startTime}ms")
                        result
                    },
                    async {
                        val startTime = System.currentTimeMillis()
                        val result = safeExecute { recipeRepository.pullFromFireStore(userId = currentUserId) }
                        val endTime = System.currentTimeMillis()
                        Log.d("SignInViewModel", "Thời gian đồng bộ recipes: ${endTime - startTime}ms")
                        result
                    },
                    async {
                        val startTime = System.currentTimeMillis()
                        val result = safeExecute {
                            mealRepository.pullFromFireStoreByUserId(userId = currentUserId)
                            dailyDiaryRepository.pullFromFireStoreByUserId(userId = currentUserId)
                        }
                        val endTime = System.currentTimeMillis()
                        Log.d("SignInViewModel", "Thời gian đồng bộ meals và daily diary: ${endTime - startTime}ms")
                        result
                    }
                ).awaitAll()
            }
        } catch (e: Exception) {
            Log.e("SignInViewModel", "Lỗi nghiêm trọng khi tải dữ liệu người dùng", e)
            throw e
        }
    }

    private suspend fun loadStaticDataIfNeeded() {
        val isStaticDataLoaded = firebaseDataManager.isStaticDataLoaded.first()
        if (!isStaticDataLoaded) {
            try {
                withContext(Dispatchers.IO) {
                    listOf(
                        async {
                            val startTime = System.currentTimeMillis()
                            val result = staticRecipeIngredientRepository.pullStaticRecipeIngredientsFromFireStore()
                            val endTime = System.currentTimeMillis()
                            Log.d("SignInViewModel", "Thời gian tải static recipe ingredients: ${endTime - startTime}ms")
                            result
                        },
//                        async {
//                            val startTime = System.currentTimeMillis()
//                            val result = staticRecipesRepository.pullStaticRecipesFromFireStore()
//                            val endTime = System.currentTimeMillis()
//                            Log.d("SignInViewModel", "Thời gian tải static recipes: ${endTime - startTime}ms")
//                            result
//                        },
//                        async {
//                            val startTime = System.currentTimeMillis()
//                            val result = staticFoodRepository.pullFromFireStore()
//                            val endTime = System.currentTimeMillis()
//                            Log.d("SignInViewModel", "Thời gian tải static foods: ${endTime - startTime}ms")
//                            result
//                        }
                    ).awaitAll()
                }
                firebaseDataManager.setStaticDataLoaded(true)
            } catch (e: Exception) {
                Log.e("SignInViewModel", "Lỗi khi tải dữ liệu tĩnh", e)
                firebaseDataManager.setStaticDataLoaded(false)
                throw e
            }
        }
    }

    private suspend fun <T> safeExecute(action: suspend () -> T): T? {
        return try {
            action()
        } catch (e: Exception) {
            Log.e("SignInViewModel", "Lỗi khi đồng bộ hóa dữ liệu", e)
            null
        }
    }
    fun pushStaticDataToFirestore() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                staticRecipeIngredientRepository.pullStaticRecipeIngredients()
                staticRecipesRepository.pullStaticRecipes()
                staticRecipesRepository.pushToFireStore()
                staticFoodRepository.pullStaticFoods()
                staticFoodRepository.pushToFireStore()
            } catch (e: Exception) {
                Log.e("SignInViewModel", "Lỗi khi đẩy dữ liệu tĩnh lên Firestore", e)
            }
        }
    }
}
