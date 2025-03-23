package com.example.btl_android_project.entity

import java.io.Serializable

data class Nutrition(
    val number: String = "",
    val name: String = "",
    val amount: Float = 0f,
    val unitName: String = ""
) : Serializable