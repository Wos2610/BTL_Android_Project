package com.example.btl_android_project.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.auth.FirebaseAuthDataSource
import com.example.btl_android_project.local.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val dataStoreManager: DataStoreManager,
) : ViewModel() {
    val currentUserId = firebaseAuthDataSource.getCurrentUserId().toString()
    private val _isNotificationsEnabled = MutableStateFlow(false)
    val isNotificationsEnabled: StateFlow<Boolean> = _isNotificationsEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            dataStoreManager.isNotificationsEnabled
                .collect { isEnabled ->
                    _isNotificationsEnabled.value = isEnabled
                }
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setNotificationsEnabled(enabled)
        }
    }

    fun logout(
        onLogoutSuccess: () -> Unit,
        onLogoutFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                firebaseAuthDataSource.logoutUser {
                    onLogoutSuccess()
                }
            } catch (e: Exception) {
                onLogoutFailure(e.message ?: "Logout failed")
            }
        }
    }
}