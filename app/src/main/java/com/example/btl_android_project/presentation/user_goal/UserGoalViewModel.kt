package com.example.btl_android_project.presentation.user_goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserGoalViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
): ViewModel() {
    var userProfileArgument: UserProfileArgument? = null

    fun calculateGoal(){
        viewModelScope.launch {
            userProfileArgument?.let {
                userProfileRepository.calculate(it)
            }
        }
    }
}