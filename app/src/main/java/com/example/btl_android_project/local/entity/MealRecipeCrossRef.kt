package com.example.btl_android_project.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "meal_recipe_cross_ref",
    primaryKeys = ["mealId", "recipeId"],
    foreignKeys = [
        ForeignKey(entity = Meal::class, parentColumns = ["id"], childColumns = ["mealId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Recipe::class, parentColumns = ["id"], childColumns = ["recipeId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class MealRecipeCrossRef(
    val mealId: Int,
    val recipeId: Int
)
