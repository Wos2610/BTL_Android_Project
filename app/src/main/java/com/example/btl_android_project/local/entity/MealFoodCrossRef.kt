package com.example.btl_android_project.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "meal_food_cross_ref",
    primaryKeys = ["mealId", "foodId"],
    foreignKeys = [
        ForeignKey(entity = Meal::class, parentColumns = ["id"], childColumns = ["mealId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Food::class, parentColumns = ["id"], childColumns = ["foodId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class MealFoodCrossRef(
    val mealId: Int,
    val foodId: Int
)
