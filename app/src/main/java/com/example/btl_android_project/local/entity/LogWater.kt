package com.example.btl_android_project.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "log_water")
data class LogWater(
    @PrimaryKey
    val id: String = "",
    val userId: String = "",
    val dailyDiaryId: String,
    val amountMl: Int = 0,
//    val logTime: String = "", // ISO 8601 format
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Serializable