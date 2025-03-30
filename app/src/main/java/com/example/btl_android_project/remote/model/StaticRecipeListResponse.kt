package com.example.btl_android_project.remote.model
import com.google.gson.annotations.SerializedName

data class StaticRecipeListResponse(
    @SerializedName("recipes")
    val recipes: RecipesContainer? = null
)

data class RecipesContainer(
    @SerializedName("max_results")
    val maxResults: String? = null,

    @SerializedName("page_number")
    val pageNumber: String? = null,

    @SerializedName("recipe")
    val recipeInRecipesContainerList: List<RecipeInRecipesContainer>? = null,

    @SerializedName("total_results")
    val totalResults: String? = null
)

data class RecipeInRecipesContainer(
    @SerializedName("recipe_description")
    val description: String? = null,
    
    @SerializedName("recipe_id")
    val id: Long? = null,
    
    @SerializedName("recipe_image")
    val imageUrl: String? = null,
    
    @SerializedName("recipe_ingredients")
    val ingredients: IngredientsInRecipesContainer? = null,
    
    @SerializedName("recipe_name")
    val name: String? = null,
    
    @SerializedName("recipe_nutrition")
    val nutrition: NutritionInRecipesContainer? = null,
    
    @SerializedName("recipe_types")
    val types: RecipeTypesInRecipesContainer? = null
)

data class IngredientsInRecipesContainer(
    @SerializedName("ingredient")
    val ingredientList: List<String>? = null
)

data class NutritionInRecipesContainer(
    @SerializedName("calories")
    val calories: String? = null,
    
    @SerializedName("carbohydrate")
    val carbohydrate: String? = null,
    
    @SerializedName("fat")
    val fat: String? = null,
    
    @SerializedName("protein")
    val protein: String? = null
)

data class RecipeTypesInRecipesContainer(
    @SerializedName("recipe_type")
    val typeList: List<String>? = null
)