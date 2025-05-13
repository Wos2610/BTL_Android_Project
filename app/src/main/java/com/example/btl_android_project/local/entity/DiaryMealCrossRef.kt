package com.example.btl_android_project.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.btl_android_project.local.enums.MealType
import java.io.Serializable

@Entity(
    tableName = "diary_meal_cross_ref",
    primaryKeys = ["diaryId", "mealId", "mealType"],
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
    val diaryId: String,
    val mealId: String,
    val userId: String,
    val servings: Int = 1,
    val mealType: MealType,
) : Serializable{
    constructor() : this("", "", "", 1, MealType.BREAKFAST)
}