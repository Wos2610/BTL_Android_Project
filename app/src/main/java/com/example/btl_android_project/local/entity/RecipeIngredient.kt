package com.example.btl_android_project.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@kotlinx.parcelize.Parcelize
@Entity(tableName = "recipe_ingredients")
data class RecipeIngredient(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fdcId: Int = 0,
    val description: String = "",
    val foodNutrients: List<Nutrition> = emptyList(),
    val numberOfServings: Int = 1,
    val servingSize: String = ""
) : Serializable, Parcelable