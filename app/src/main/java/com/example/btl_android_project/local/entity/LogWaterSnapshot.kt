package com.example.btl_android_project.local.entity

import java.io.Serializable

data class LogWaterSnapshot(
    val id: String = "",
    val userId: String = "",
    val dailyDiaryId: String,
    val amountMl: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Serializable