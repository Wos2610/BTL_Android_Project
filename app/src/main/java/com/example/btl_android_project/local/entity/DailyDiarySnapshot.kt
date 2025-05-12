package com.example.btl_android_project.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.btl_android_project.local.Converters
import java.io.Serializable
import java.time.LocalDate

@Entity(tableName = "daily_diary_snapshots")
@TypeConverters(Converters::class)
data class DailyDiarySnapshot(
    @PrimaryKey
    val id: String = "",
    val diaryId: String,
    val userId: String,
    val logDate: LocalDate,
    
    // Thông tin từ DailyDiary
    val caloriesRemaining: Float,
    val totalFoodCalories: Float,
    val totalExerciseCalories: Float,
    val totalWaterMl: Int,
    val totalFat: Float,
    val totalCarbs: Float,
    val totalProtein: Float,
    val caloriesGoal: Float,

    val foods: List<DairyFoodSnapshot> = emptyList(),

    val meals: List<DiaryMealSnapshot> = emptyList(),

    val recipes: List<DiaryRecipeSnapshot> = emptyList(),

    val waters : List<LogWaterSnapshot> = emptyList(),

    val exercises : List<LogExerciseSnapshot> = emptyList(),
    
    val createdAt: Long = System.currentTimeMillis(),
    val syncedToFirestore: Boolean = false
) : Serializable
