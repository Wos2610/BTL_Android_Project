package com.example.btl_android_project.local.entity

import java.io.Serializable

data class Exercise(
    val id: Int = 0,
    val name: String = "",
    val caloriesPerMinute: Float = 0f
) : Serializable