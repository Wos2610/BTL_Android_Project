package com.example.btl_android_project.entity

import java.io.Serializable

data class StaticFood(
    val id: Int = 0,
    val name: String = "",
    val calories: Float = 0f,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f
) : Serializable