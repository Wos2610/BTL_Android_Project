package com.example.btl_android_project.presentation.log_all

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogAllViewModel @Inject constructor(): ViewModel() {
    var isFromCreateMeal = false
}