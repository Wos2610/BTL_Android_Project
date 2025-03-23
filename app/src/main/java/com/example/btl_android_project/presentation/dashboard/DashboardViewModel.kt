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

    fun addUser(){
        userRepository.addUser(1, "username", "email", "password")
    }

    fun addAllRecipeIngredients(context: Context){
        val recipeIngredients = readJsonFile("ingredients.json", context)
        println("Adding ${recipeIngredients.size} ingredients")
        staticRecipeIngredientRepository.addAllRecipeIngredients(recipeIngredients)
    }

    private fun readJsonFile(fileName: String, context: Context): List<StaticRecipeIngredient> {
        return try {
            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.readText()
            reader.close()

            val listType = object : TypeToken<List<StaticRecipeIngredient>>() {}.type
            val list = Gson().fromJson<List<StaticRecipeIngredient>>(jsonString, listType)

            Timber.d("Total items read: ${list.size}")
            list
        } catch (e: Exception) {
            Timber.e("Error reading JSON: ${e.message}")
            emptyList()
        }
    }

    fun getStaticRecipeIngredients() {
        viewModelScope.launch {
            val result = staticRecipeIngredientRepository.getStaticRecipeIngredients()
            result.onSuccess {
                Timber.d("Static recipe ingredients: ${it?.size}")
            }.onError { error ->
                Timber.e("Error getting static recipe ingredients: ${error}")
            }.onException { exeption ->
                Timber.e("Exception getting static recipe ingredients: ${exeption}")
            }
        }
    }


}