package com.example.btl_android_project.local.entity

import java.io.Serializable

data class LogWater(
    val id: Int = 0,
    val userId: Int = 0,
    val dailyDiaryId: Int = 0,
    val amountMl: Int = 0,
    val logTime: String = ""
) : Serializable