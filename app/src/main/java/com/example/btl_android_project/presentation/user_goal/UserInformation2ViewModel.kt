package com.example.btl_android_project.presentation.user_goal

import androidx.lifecycle.ViewModel

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserInformation2ViewModel @Inject constructor(): ViewModel() {
    var userProfileArgument: UserProfileArgument? = null
    var userHeightCm: Double = 0.0
    var userCurrentWeight: Double = 0.0
    var userGoalWeight: Double = 0.0

    fun updateUserProfileArgumentBeforeSend() {
        userProfileArgument = userProfileArgument?.copy(
            heightCm = userHeightCm,
            currentWeight = userCurrentWeight,
            goalWeight = userGoalWeight
        )
    }
}