package com.example.btl_android_project.entity

import java.io.Serializable

data class Recipe(
    val id: Int = 0,
    val name: String = "",
    val imageUrl: String = "",
    val instructions: String = "",
    val calories: Float = 0f
) : Serializable