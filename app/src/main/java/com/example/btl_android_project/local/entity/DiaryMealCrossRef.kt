package com.example.btl_android_project.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.btl_android_project.local.MealType

@Entity(
    tableName = "diary_meal_cross_ref",
    primaryKeys = ["diaryId", "mealId"],
    foreignKeys = [
        ForeignKey(
            entity = DailyDiary::class,
            parentColumns = ["id"],
            childColumns = ["diaryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Meal::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DiaryMealCrossRef(
    val diaryId: Int,
    val mealId: Int,
    val servings: Int = 1,
    val mealType: MealType? = null,
)