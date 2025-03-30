package com.example.btl_android_project.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "static_foods")
data class StaticFoodEntity(
    @PrimaryKey val foodId: Long,
    val foodName: String?,
    val foodType: String?,
    val foodUrl: String?,
    val servings: String?
)