package com.example.btl_android_project.presentation.user_goal

import androidx.lifecycle.ViewModel

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserInformation2ViewModel @Inject constructor(): ViewModel() {
    var userProfileArgument: UserProfileArgument? = null
    var userHeightCm: Float = 0f
    var userCurrentWeight: Float = 0f
    var userGoalWeight: Float = 0f

    fun updateUserProfileArgumentBeforeSend() {
        userProfileArgument = userProfileArgument?.copy(
            heightCm = userHeightCm,
            currentWeight = userCurrentWeight,
            goalWeight = userGoalWeight
        )
    }
}