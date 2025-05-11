package com.example.btl_android_project.local.entity

import java.io.Serializable

data class NutritionSnapshot(
    val number: String,
    val name: String,
    val amount: Float,
    val unitName: String
) : Serializable