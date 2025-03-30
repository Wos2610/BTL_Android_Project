package com.example.btl_android_project.local.entity

import java.io.Serializable

data class LogExercise(
    val id: Int = 0,
    val userId: Int = 0,
    val exerciseId: Int = 0,
    val dailyDiaryId: Int = 0,
    val minutes: Int = 0,
    val totalCalories: Float = 0f,
    val logTime: String = ""
) : Serializable