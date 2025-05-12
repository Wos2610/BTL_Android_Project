package com.example.btl_android_project.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey
    val id: String = "",
    val userId: String = "",
    val description: String = "",
    val minutesPerformed: Int = 0,
    val caloriesBurned: Float = 0f
) : Serializable