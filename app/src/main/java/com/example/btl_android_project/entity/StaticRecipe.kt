package com.example.btl_android_project.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "static_recipes")
data class StaticRecipe(
    @PrimaryKey
    @ColumnInfo(name = "recipe_id")
    val recipeId: Int,

    @ColumnInfo(name = "recipe_name")
    val recipeName: String,

    @ColumnInfo(name = "recipe_url")
    val recipeUrl: String? = null,

    @ColumnInfo(name = "recipe_description")
    val recipeDescription: String? = null,

    @ColumnInfo(name = "number_of_servings")
    val numberOfServings: Int,

    @ColumnInfo(name = "grams_per_portion")
    val gramsPerPortion: Double,

    @ColumnInfo(name = "preparation_time_min")
    val preparationTimeMin: Int,

    @ColumnInfo(name = "cooking_time_min")
    val cookingTimeMin: Int,

    @ColumnInfo(name = "rating")
    val rating: Int? = null,

    @ColumnInfo(name = "recipe_types")
    val recipeTypes: String,

    @ColumnInfo(name = "recipe_categories")
    val recipeCategories: String,

    @ColumnInfo(name = "recipe_image")
    val recipeImage: String? = null,

    @ColumnInfo(name = "serving_details")
    val servingDetails: String,

    @ColumnInfo(name = "ingredients")
    val ingredients: String,

    @ColumnInfo(name = "directions")
    val directions: String
) {
    fun getRecipeTypesList(): List<String> {
        return listOf(recipeTypes)
    }

    fun getRecipeCategoriesList(): List<RecipeCategory> {
        val type = object : TypeToken<List<RecipeCategory>>() {}.type
        return Gson().fromJson(recipeCategories, type)
    }

    fun getServingDetailList(): ServingDetails {
        return Gson().fromJson(servingDetails, ServingDetails::class.java)
    }

    fun getIngredientsList(): List<Ingredient> {
        val type = object : TypeToken<List<Ingredient>>() {}.type
        return Gson().fromJson(ingredients, type)
    }

    fun getDirectionsList(): List<Direction> {
        val type = object : TypeToken<List<Direction>>() {}.type
        return Gson().fromJson(directions, type)
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
    val protein: Double,
    val fat: Double,
    val fiber: Double
)

data class Ingredient(
    val foodId: Int,
    val foodName: String,
    val numberOfUnits: Double,
    val measurementDescription: String,
    val ingredientDescription: String
)

data class Direction(
    val directionNumber: Int,
    val directionDescription: String
)

