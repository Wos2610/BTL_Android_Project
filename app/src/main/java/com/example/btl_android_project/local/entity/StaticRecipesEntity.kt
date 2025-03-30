package com.example.btl_android_project.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "static_recipes")
data class StaticRecipesEntity(
    @PrimaryKey
    @SerializedName("recipe_id") val recipeId: Long,
    @SerializedName("cooking_time_min") val cookingTimeMin: String?,
    @SerializedName("directions") val directions: String?, // JSON string from Directions
    @SerializedName("grams_per_portion") val gramsPerPortion: String?,
    @SerializedName("ingredients") val ingredients: String?, // JSON string from Ingredients
    @SerializedName("number_of_servings") val numberOfServings: String?,
    @SerializedName("preparation_time_min") val preparationTimeMin: String?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("recipe_categories") val recipeCategories: String?, // JSON string from RecipeCategories
    @SerializedName("recipe_description") val recipeDescription: String?,
    @SerializedName("recipe_images") val recipeImages: String?, // JSON string from RecipeImages
    @SerializedName("recipe_name") val recipeName: String?,
    @SerializedName("recipe_types") val recipeTypes: String?, // JSON string from RecipeTypes
    @SerializedName("recipe_url") val recipeUrl: String?,
    @SerializedName("serving_sizes") val servingSizes: String? // JSON string from ServingSizes
)