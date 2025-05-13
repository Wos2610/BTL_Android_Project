package com.example.btl_android_project.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class DiaryWithAllNutrition(
    @Embedded val diary: DailyDiary,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = DiaryMealCrossRef::class,
            parentColumn = "diaryId",
            entityColumn = "mealId"
        )
    )
    val meals: List<Meal>,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = DiaryRecipeCrossRef::class,
            parentColumn = "diaryId",
            entityColumn = "recipeId"
        )
    )
    val recipes: List<Recipe>,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = DiaryFoodCrossRef::class,
            parentColumn = "diaryId",
            entityColumn = "foodId"
        )
    )
    val foods: List<Food>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = DiaryExerciseCrossRef::class,
            parentColumn = "diaryId",
            entityColumn = "exerciseId"
        )
    )
    val exercises: List<Exercise>,

    @Relation(
        parentColumn = "id",
        entityColumn = "dailyDiaryId"
    )
    val waterLogs: List<LogWater>
)
