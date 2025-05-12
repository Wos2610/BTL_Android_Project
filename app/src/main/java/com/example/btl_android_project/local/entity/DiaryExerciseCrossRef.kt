package com.example.btl_android_project.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "diary_exercise_cross_ref",
    primaryKeys = ["diaryId", "exerciseId"],
    foreignKeys = [
        ForeignKey(
            entity = DailyDiary::class,
            parentColumns = ["id"],
            childColumns = ["diaryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DiaryExerciseCrossRef(
    val diaryId: String,
    val exerciseId: String,
    val userId: String,
    var servings: Int = 1
) {
    constructor(): this("", "", "", 1)
}