package com.example.btl_android_project.local.entity

import java.io.Serializable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int = 0,
    val name: String = "",
    val mealType: String = "",
    val totalCalories: Float = 0f
) : Serializable
