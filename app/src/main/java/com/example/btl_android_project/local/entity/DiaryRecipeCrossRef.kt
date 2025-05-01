package com.example.btl_android_project.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.btl_android_project.local.MealType

@Entity(
    tableName = "diary_recipe_cross_ref",
    primaryKeys = ["diaryId", "recipeId"],
    foreignKeys = [
        ForeignKey(
            entity = DailyDiary::class,
            parentColumns = ["id"],
            childColumns = ["diaryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DiaryRecipeCrossRef(
    val diaryId: Int,
    val recipeId: Int,
    val userId: Int,
    val servings: Int = 1,
    val mealType: MealType? = null,
)