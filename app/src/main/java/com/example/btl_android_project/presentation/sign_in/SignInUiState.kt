package com.example.btl_android_project.presentation.sign_in

sealed class SignInUiState {
    object Idle : SignInUiState()
    object Loading : SignInUiState()
    object Success : SignInUiState()
    object NeedsSetup : SignInUiState()
    object NotLoggedIn : SignInUiState()
    data class Error(val exception: Exception) : SignInUiState()
}