package com.example.btl_android_project.presentation.sign_in

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.auth.FirebaseAuthDataSource
import com.example.btl_android_project.repository.DailyDiaryRepository
import com.example.btl_android_project.repository.FoodRepository
import com.example.btl_android_project.repository.MealRepository
import com.example.btl_android_project.repository.RecipeRepository
import com.example.btl_android_project.repository.StaticFoodsRepository
import com.example.btl_android_project.repository.StaticRecipeIngredientRepository
import com.example.btl_android_project.repository.StaticRecipesRepository
import com.example.btl_android_project.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val staticRecipeIngredientRepository: StaticRecipeIngredientRepository,
    private val staticRecipesRepository: StaticRecipesRepository,
    private val staticFoodRepository: StaticFoodsRepository,
    private val foodRepository: FoodRepository,
    private val recipeRepository: RecipeRepository,
    private val mealRepository: MealRepository,
    private val dailyDiaryRepository: DailyDiaryRepository,
    private val authDataSource: FirebaseAuthDataSource,
    private val userProfileRepository: UserProfileRepository,
): ViewModel() {
    fun loginUser(
        email: String,
        password: String,
        onLogin: () -> Unit,
        onFailure: (Exception) -> Unit,
        onLoading: (Boolean) -> Unit,
        onSetUpGoal: () -> Unit
    ) {
        if(email == "" || password == "") {
            onFailure(Exception("Email or password cannot be empty"))
            return
        }
        onLoading(true)
        firebaseAuthDataSource.loginUser(
            email = email,
            password = password,
            onSuccess = {
                checkUserSetUpGoal(
                    onLoadUserData = {
                        loadUserData(
                            onSuccess = onLogin,
                            onLoading = onLoading
                        )
                    },
                    onSetUpGoal = {
                        onLoading(false)
                        onSetUpGoal()
                    },
                    onLoading = onLoading
                )
            },
            onFailure = { exception ->
                onLoading(false)
                onFailure(exception)
            }
        )
    }

    fun checkUserLoggedIn(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
        onLoading: (Boolean) -> Unit
    ) {
        onLoading(true)
        firebaseAuthDataSource.checkUserLoggedIn(
            onSuccess = {
                loadUserData(
                    onSuccess = onSuccess,
                    onLoading = onLoading
                )
            },
            onFailure = { exception ->
                onLoading(false)
                onFailure(exception)
            }
        )
    }

    fun checkUserSetUpGoal(
        onLoadUserData: () -> Unit,
        onSetUpGoal: () -> Unit,
        onLoading: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            onLoading(true)
            try {
                val currentUserId = firebaseAuthDataSource.getCurrentUserId().toString()
                val userProfile = userProfileRepository.getUserProfileByUserId(
                    userId = currentUserId,
                )

                if (userProfile != null) {
                    onLoadUserData()
                } else {
                    onSetUpGoal()
                }
                onLoading(false)

            } catch (e: Exception) {
                Log.e("SignInViewModel", "Error checking user set up goal", e)
            }
        }
    }

    fun loadUserData(
        onSuccess: () -> Unit,
        onLoading: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
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
                // Handle any unexpected errors
            } finally {
                onLoading(false)
            }
        }
    }
}