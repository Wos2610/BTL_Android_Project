package com.example.btl_android_project.presentation.dashboard

import androidx.lifecycle.ViewModel
import com.example.btl_android_project.entity.StaticRecipeIngredient
import com.example.btl_android_project.repository.StaticRecipeIngredientRepository
import com.example.btl_android_project.repository.UserRepository
import com.google.common.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.remote.onError
import com.example.btl_android_project.remote.onException
import com.example.btl_android_project.remote.onSuccess
import com.google.gson.Gson
import timber.log.Timber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
) : ViewModel() {
    val userRepository = UserRepository()
    @Inject lateinit var staticRecipeIngredientRepository: StaticRecipeIngredientRepository

    fun pullStaticRecipeIngredients() {
        viewModelScope.launch {
            staticRecipeIngredientRepository.pullStaticRecipeIngredients()
        }
    }


}