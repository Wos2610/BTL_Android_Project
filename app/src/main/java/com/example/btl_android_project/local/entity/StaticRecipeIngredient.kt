package com.example.btl_android_project.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@kotlinx.parcelize.Parcelize
@Entity(tableName = "static_recipe_ingredients")
data class StaticRecipeIngredient(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fdcId: Int = 0,
    val description: String = "",
    val dataType: String = "",
    val publicationDate: String = "",
    val foodCode: String = "",
    val foodNutrients: List<Nutrition> = emptyList()
) : Serializable, Parcelable
