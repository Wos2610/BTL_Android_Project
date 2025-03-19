package com.example.btl_android_project.entity

import java.io.Serializable

data class RecipeIngredient(
    val id: Int = 0,
    val recipeId: Int = 0,
    val quantity: Float = 0f,
    val unit: String = ""
) : Serializable