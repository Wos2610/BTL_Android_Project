package com.example.btl_android_project.local.entity
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class MealWithFoodsAndRecipes(
    @Embedded val meal: Meal,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = MealFoodCrossRef::class,
            parentColumn = "mealId",
            entityColumn = "foodId"
        )
    )
    val foods: List<Food> = emptyList(),

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = MealRecipeCrossRef::class,
            parentColumn = "mealId",
            entityColumn = "recipeId"
        )
    )
    val recipes: List<Recipe> = emptyList()
)
