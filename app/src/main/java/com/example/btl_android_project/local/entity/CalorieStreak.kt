package com.example.btl_android_project.local.entity

import java.io.Serializable

data class CalorieStreak(
    val userId: Int = 0,
    val streakDays: Int = 0
) : Serializable