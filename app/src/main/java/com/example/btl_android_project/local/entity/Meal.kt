package com.example.btl_android_project.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val mealType: String = "",
    var totalCalories: Float = 0f,
    var totalCarbs: Float = 0f,
    var totalProtein: Float = 0f,
    var totalFat: Float = 0f,
    val isDeleted: Boolean = false,
) : Serializable
