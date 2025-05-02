package com.example.btl_android_project.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val instructions: String = "",
    val calories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0,
    val userId: String = "",
    val ingredients: List<RecipeIngredient> = emptyList(),
    val servings: Int = 0,
) : Serializable