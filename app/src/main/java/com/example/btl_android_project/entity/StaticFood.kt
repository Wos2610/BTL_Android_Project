package com.example.btl_android_project.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "static_foods")
data class StaticFoodEntity(
    @PrimaryKey val foodId: Long,
    val foodName: String?,
    val foodType: String?,
    val foodUrl: String?,
    val servings: String? // Lưu danh sách serving dưới dạng JSON string
)