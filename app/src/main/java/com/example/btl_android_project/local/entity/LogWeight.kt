package com.example.btl_android_project.local.entity

import java.io.Serializable

data class LogWeight(
    val id: Int = 0,
    val userId: String = "",
    val dailyDiaryId: Int = 0,
    val weight: Float = 0f,
    val logTime: String = ""
) : Serializable