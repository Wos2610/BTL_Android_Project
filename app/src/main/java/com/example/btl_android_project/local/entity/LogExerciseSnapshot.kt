package com.example.btl_android_project.local.entity

data class LogExerciseSnapshot(
    val id: String = "",
    val userId: String = "",
    val dailyDiaryId: String,
    val exerciseId: String,
    val duration: Int = 0,
    val calories: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
