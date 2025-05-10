package com.example.btl_android_project.local.entity

import androidx.room.Entity
import java.io.Serializable
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey
    val userId: String = "",
    val height: Float = 0f,
    val currentWeight: Float = 0f,
    val initialWeight: Float = 0f,
    val weeklyGoal: String = "",
    val weightGoal: Float = 0f,
    val waterGoal: Int = 0,
    val calorieGoal: Int = 0,
) : Serializable