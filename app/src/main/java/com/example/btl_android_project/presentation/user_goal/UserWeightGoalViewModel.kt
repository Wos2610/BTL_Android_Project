package com.example.btl_android_project.presentation.user_goal

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserWeightGoalViewModel @Inject constructor(): ViewModel() {
    var userGoal: String = ""
    var userProfileArgument : UserProfileArgument = UserProfileArgument()

    fun updateUserProfileArgumentBeforeSend() {
        userProfileArgument = userProfileArgument.copy(
            weightGoal = userGoal
        )
    }
}