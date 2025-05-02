package com.example.btl_android_project.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate


@Entity(tableName = "daily_diary")
data class DailyDiary(
    @PrimaryKey
    val id: String = "",
    val userId: String = "",
    val logDate: LocalDate? = null,
    val caloriesRemaining: Float = 0f,
    val totalFoodCalories: Float = 0f,
    val totalExerciseCalories: Float = 0f,
    val totalWaterMl: Int = 0,
    val totalFat: Float = 0f,
    val totalCarbs: Float = 0f,
    val totalProtein: Float = 0f,
) : Serializable