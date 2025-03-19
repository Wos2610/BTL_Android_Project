package com.example.btl_android_project.entity

import java.io.Serializable

data class StaticRecipeIngredient(
    val id: Int = 0,
    val quantity: Float = 0f,
    val unit: String = ""
) : Serializable