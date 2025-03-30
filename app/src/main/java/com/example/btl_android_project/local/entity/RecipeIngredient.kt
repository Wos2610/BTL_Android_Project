package com.example.btl_android_project.local.entity

import java.io.Serializable

data class RecipeIngredient(
    val id: Int = 0,
    val recipeId: Int = 0,
    val quantity: Double = 0.0,
    val unit: String = ""
) : Serializable