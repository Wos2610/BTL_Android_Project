package com.example.btl_android_project.presentation.log_recipe

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewRecipeViewModel @Inject constructor() : ViewModel() {
    var recipeName: String = ""
    var servings: Int = 1
}