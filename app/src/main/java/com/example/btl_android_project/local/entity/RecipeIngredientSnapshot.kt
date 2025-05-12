package com.example.btl_android_project.local.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class RecipeIngredientSnapshot(
    val id: String,
    val fdcId: Int,
    val description: String,
    val foodNutrients: List<NutritionSnapshot> = emptyList(),
    val numberOfServings: Int,
    val servingSize: String
) : Serializable, Parcelable