package com.example.btl_android_project.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.btl_android_project.local.MealType

@Entity(
    tableName = "diary_food_cross_ref",
    primaryKeys = ["diaryId", "foodId"],
    foreignKeys = [
        ForeignKey(
            entity = DailyDiary::class,
            parentColumns = ["id"],
            childColumns = ["diaryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Food::class,
            parentColumns = ["id"],
            childColumns = ["foodId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
)
data class DiaryFoodCrossRef(
    val diaryId: Int,
    val foodId: Int,
    val userId: Int,
    val servings: Int = 1,
    val mealType: MealType? = null,
)