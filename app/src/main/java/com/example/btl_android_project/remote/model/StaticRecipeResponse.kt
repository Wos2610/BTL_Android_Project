package com.example.btl_android_project.remote.model

import com.google.gson.annotations.SerializedName

data class StaticRecipeResponse(
    @SerializedName("recipe") val recipe: StaticRecipe? = null
)

data class StaticRecipe(
    @SerializedName("cooking_time_min") val cookingTimeMin: String? = null,
    @SerializedName("directions") val directions: Directions? = null,
    @SerializedName("grams_per_portion") val gramsPerPortion: String? = null,
    @SerializedName("ingredients") val ingredients: Ingredients? = null,
    @SerializedName("number_of_servings") val numberOfServings: String? = null,
    @SerializedName("preparation_time_min") val preparationTimeMin: String? = null,
    @SerializedName("rating") val rating: String? = null,
    @SerializedName("recipe_categories") val recipeCategories: RecipeCategories? = null,
    @SerializedName("recipe_description") val recipeDescription: String? = null,
    @SerializedName("recipe_id") val recipeId: Long? = null,
    @SerializedName("recipe_images") val recipeImages: RecipeImages? = null,
    @SerializedName("recipe_name") val recipeName: String? = null,
    @SerializedName("recipe_types") val recipeTypes: RecipeTypes? = null,
    @SerializedName("recipe_url") val recipeUrl: String? = null,
    @SerializedName("serving_sizes") val servingSizes: ServingSizes? = null
)

data class Directions(
    @SerializedName("direction") val direction: List<Direction?>? = null
)

data class Direction(
    @SerializedName("direction_description") val directionDescription: String? = null,
    @SerializedName("direction_number") val directionNumber: String? = null
)

data class Ingredients(
    @SerializedName("ingredient") val ingredient: List<Ingredient?>? = null
)

data class Ingredient(
    @SerializedName("food_id") val foodId: String? = null,
    @SerializedName("food_name") val foodName: String? = null,
    @SerializedName("ingredient_description") val ingredientDescription: String? = null,
    @SerializedName("ingredient_url") val ingredientUrl: String? = null,
    @SerializedName("measurement_description") val measurementDescription: String? = null,
    @SerializedName("number_of_units") val numberOfUnits: String? = null,
    @SerializedName("serving_id") val servingId: String? = null
)

data class RecipeCategories(
    @SerializedName("recipe_category") val recipeCategory: List<RecipeCategory?>? = null
)

data class RecipeCategory(
    @SerializedName("recipe_category_name") val recipeCategoryName: String? = null,
    @SerializedName("recipe_category_url") val recipeCategoryUrl: String? = null
)

data class RecipeImages(
    @SerializedName("recipe_image") val recipeImage: List<String?>? = null
)

data class RecipeTypes(
    @SerializedName("recipe_type") val recipeType: List<String?>? = null
)

data class ServingSizes(
    @SerializedName("serving") val serving: Serving? = null
)

data class Serving(
    @SerializedName("calcium") val calcium: String? = null,
    @SerializedName("calories") val calories: String? = null,
    @SerializedName("carbohydrate") val carbohydrate: String? = null,
    @SerializedName("cholesterol") val cholesterol: String? = null,
    @SerializedName("fat") val fat: String? = null,
    @SerializedName("fiber") val fiber: String? = null,
    @SerializedName("iron") val iron: String? = null,
    @SerializedName("monounsaturated_fat") val monounsaturatedFat: String? = null,
    @SerializedName("polyunsaturated_fat") val polyunsaturatedFat: String? = null,
    @SerializedName("potassium") val potassium: String? = null,
    @SerializedName("protein") val protein: String? = null,
    @SerializedName("saturated_fat") val saturatedFat: String? = null,
    @SerializedName("serving_size") val servingSize: String? = null,
    @SerializedName("sodium") val sodium: String? = null,
    @SerializedName("sugar") val sugar: String? = null,
    @SerializedName("trans_fat") val transFat: String? = null,
    @SerializedName("vitamin_a") val vitaminA: String? = null,
    @SerializedName("vitamin_c") val vitaminC: String? = null
)