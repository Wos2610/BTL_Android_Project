package com.example.btl_android_project.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.annotations.SerializedName

val gson = Gson()
@Entity(tableName = "static_recipes")
data class StaticRecipe(
    @PrimaryKey
    @ColumnInfo(name = "recipe_id")
    @SerializedName("recipe_id")
    val recipeId: Int,

    @ColumnInfo(name = "recipe_name")
    @SerializedName("recipe_name")
    val recipeName: String,

    @ColumnInfo(name = "recipe_url")
    @SerializedName("recipe_url")
    val recipeUrl: String? = null,

    @ColumnInfo(name = "recipe_description")
    @SerializedName("recipe_description")
    val recipeDescription: String? = null,

    @ColumnInfo(name = "number_of_servings")
    @SerializedName("number_of_servings")
    val numberOfServings: Int,

    @ColumnInfo(name = "grams_per_portion")
    @SerializedName("grams_per_portion")
    val gramsPerPortion: Double,

    @ColumnInfo(name = "preparation_time_min")
    @SerializedName("preparation_time_min")
    val preparationTimeMin: Int,

    @ColumnInfo(name = "cooking_time_min")
    @SerializedName("cooking_time_min")
    val cookingTimeMin: Int,

    @ColumnInfo(name = "rating")
    @SerializedName("rating")
    val rating: Int? = null,

    @ColumnInfo(name = "recipe_types")
    @SerializedName("recipe_types")
    val recipeTypes: String,  // JSON String, use helper to convert

    @ColumnInfo(name = "recipe_categories")
    @SerializedName("recipe_categories")
    val recipeCategories: String,  // JSON String, use helper to convert

    @ColumnInfo(name = "recipe_images")
    @SerializedName("recipe_images")
    val recipeImages: String,  // JSON String, use helper to convert

    @ColumnInfo(name = "serving_sizes")
    val servingDetails: String,  // JSON String, use helper to convert

    @ColumnInfo(name = "ingredients")
    @SerializedName("ingredients")
    val ingredients: String,  // JSON String, use helper to convert

    @ColumnInfo(name = "directions")
    @SerializedName("directions")
    val directions: String  // JSON String, use helper to convert
) {
    fun getRecipeTypesList(): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(recipeTypes, type)
    }

    fun getRecipeCategoriesList(): List<RecipeCategory> {
        val type = object : TypeToken<List<RecipeCategory>>() {}.type
        return gson.fromJson(recipeCategories, type)
    }

    fun getServingDetailList(): ServingDetails {
        return gson.fromJson(servingDetails, ServingDetails::class.java)
    }

    fun getIngredientsList(): List<Ingredient> {
        val type = object : TypeToken<List<Ingredient>>() {}.type
        return gson.fromJson(ingredients, type)
    }

    fun getDirectionsList(): List<Direction> {
        val type = object : TypeToken<List<Direction>>() {}.type
        return gson.fromJson(directions, type)
    }

    fun getRecipeImagesList(): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(recipeImages, type)
    }
}

data class RecipeCategory(
    val recipeCategoryName: String,
    val recipeCategoryUrl: String
)

data class ServingDetails(
    val servingSize: String,
    val calories: Int,
    val carbohydrate: Double,
    val cholesterol: Int,
    val fat: Double,
    val fiber: Double,
    val iron: Int,
    val monounsaturatedFat: Double,
    val polyunsaturatedFat: Double,
    val potassium: Int,
    val protein: Double,
    val saturatedFat: Double,
    val sodium: Int,
    val sugar: Double,
    val transFat: Double,
    val vitaminA: Int,
    val vitaminC: Int
)

data class Ingredient(
    @SerializedName("food_id")
    val foodId: String,
    @SerializedName("food_name")
    val foodName: String,
    @SerializedName("ingredient_description")
    val ingredientDescription: String,
    @SerializedName("ingredient_url")
    val ingredientUrl: String,
    @SerializedName("measurement_description")
    val measurementDescription: String,
    @SerializedName("number_of_units")
    val numberOfUnits: String,
    @SerializedName("serving_id")
    val servingId: String
)

data class Direction(
    @SerializedName("direction_number")
    val directionNumber: Int,
    @SerializedName("direction_description")
    val directionDescription: String
)

