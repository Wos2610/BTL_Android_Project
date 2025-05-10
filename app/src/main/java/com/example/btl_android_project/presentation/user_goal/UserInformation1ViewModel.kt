package com.example.btl_android_project.presentation.user_goal

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserInformation1ViewModel @Inject constructor(): ViewModel() {
    var userProfileArgument: UserProfileArgument? = null
    var isFeMale: Boolean = true
    var dateOfBirth: String = ""
    var city: String = ""

    fun updateUserProfileArgumentBeforeSend() {
        userProfileArgument = userProfileArgument?.copy(
            dateOfBirth = dateOfBirth,
            city = city,
            isFeMale = isFeMale
        )
    }
}