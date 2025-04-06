package com.example.btl_android_project.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.btl_android_project.local.Converters
import java.io.Serializable

@Entity(tableName = "foods")
@TypeConverters(Converters::class)
data class Food(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val calories: Float = 0f,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,

    val description: String = "",
    val servingsSize: Int = 0,
    val servingsUnit: String = "",
    val servingsPerContainer: Int = 0,

    val nutritions: List<Nutrition> = emptyList(),
    val userId: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Serializable
