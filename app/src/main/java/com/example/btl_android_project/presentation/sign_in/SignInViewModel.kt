package com.example.btl_android_project.presentation.sign_in

import androidx.lifecycle.ViewModel
import com.example.btl_android_project.auth.FirebaseAuthDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
     private val firebaseAuthDataSource: FirebaseAuthDataSource
): ViewModel() {
    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if(email == "" || password == "") {
            onFailure(Exception("Email or password cannot be empty"))
            return
        }

        firebaseAuthDataSource.loginUser(email, password, onSuccess, onFailure)
    }

    fun checkUserLoggedIn(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebaseAuthDataSource.checkUserLoggedIn(
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}