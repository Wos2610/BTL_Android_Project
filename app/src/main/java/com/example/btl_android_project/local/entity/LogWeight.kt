package com.example.btl_android_project.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "log_weight")
data class LogWeight(
    @PrimaryKey
    val id: String = "",
    val userId: String = "",
    val weight: Float = 0f,
    val date: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Serializable
