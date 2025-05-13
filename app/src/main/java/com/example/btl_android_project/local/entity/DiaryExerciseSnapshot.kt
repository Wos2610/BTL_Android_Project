package com.example.btl_android_project.local.entity

import java.io.Serializable

data class DiaryExerciseSnapshot(
    val exerciseId: String = "",
    val userId: String = "",
    val description: String = "",
    val minutesPerformed: Int = 0,
    val caloriesBurned: Float = 0f,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val servings: Int = 1
) : Serializable