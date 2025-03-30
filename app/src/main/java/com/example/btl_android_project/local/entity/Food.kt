package com.example.btl_android_project.local.entity

import java.io.Serializable

data class Food(
    val id: Int = 0,
    val name: String = "",
    val calories: Float = 0f,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,
    val userId: Int = 0
) : Serializable