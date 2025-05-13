package com.example.btl_android_project.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.btl_android_project.local.enums.MealType
import java.io.Serializable

@Entity(
    tableName = "diary_food_cross_ref",
    primaryKeys = ["diaryId", "foodId", "mealType"],
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
    val diaryId: String,
    val foodId: String,
    val userId: String,
    var servings: Int = 1,
    val mealType: MealType,
) : Serializable {
    constructor() : this("", "", "", 1, MealType.BREAKFAST)
}