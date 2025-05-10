package com.example.btl_android_project.presentation.user_goal

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserActivityLevelViewModel @Inject constructor() : ViewModel() {
    var userProfileArgument: UserProfileArgument? = null
    var userActivityLevel: String = ""

    fun updateUserProfileArgumentBeforeSend() {
        userProfileArgument = userProfileArgument?.copy(
            activityLevel = userActivityLevel
        )
    }
}