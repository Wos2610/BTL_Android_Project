package com.example.btl_android_project.local.entity

import androidx.room.PrimaryKey
import java.io.Serializable

data class DiaryExerciseSnapshot(
    val id: String = "",
    val userId: String = "",
    val description: String = "",
    val minutesPerformed: Int = 0,
    val caloriesBurned: Float = 0f
) : Serializable