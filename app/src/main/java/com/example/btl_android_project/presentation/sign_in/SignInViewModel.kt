package com.example.btl_android_project.presentation.sign_in

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
    private val authDataSource: FirebaseAuthDataSource
): ViewModel() {
    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
        onLoading: (Boolean) -> Unit
    ) {
        if(email == "" || password == "") {
            onFailure(Exception("Email or password cannot be empty"))
            return
        }

        firebaseAuthDataSource.loginUser(
            email = email,
            password = password,
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