package com.example.btl_android_project.entity

import java.io.Serializable

data class Nutrition(
    val id: Int = 0,
    val foodId: Int? = null,
    val recipeId: Int? = null,
    val recipeIngredientId: Int? = null,
    val calories: Float = 0f,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,
    val fiber: Float = 0f,
    val sugar: Float = 0f,
    val sodium: Float = 0f
) : Serializable