package com.example.btl_android_project.presentation.sign_up

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.auth.FirebaseAuthDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource
): ViewModel() {
    fun registerUser(
        email: String,
        password: String,
        confirmPassword: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if(email == "" || password == "" || confirmPassword == "") {
            onFailure(Exception("Email or password cannot be empty"))
            return
        }

        if(password != confirmPassword) {
            onFailure(Exception("Password and confirmation do not match"))
            return
        }

        viewModelScope.launch {
            firebaseAuthDataSource.registerUser(email, password, onSuccess, onFailure)
        }
    }
}