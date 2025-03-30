package com.example.btl_android_project.utils

import com.example.btl_android_project.local.entity.StaticFoodEntity
import com.example.btl_android_project.remote.model.Directions
import com.example.btl_android_project.remote.model.Ingredients
import com.example.btl_android_project.remote.model.RecipeCategories
import com.example.btl_android_project.remote.model.RecipeImages
import com.example.btl_android_project.remote.model.RecipeTypes
import com.example.btl_android_project.remote.model.ServingSizes
import com.example.btl_android_project.remote.model.StaticRecipe
import com.example.btl_android_project.local.entity.StaticRecipesEntity
import com.example.btl_android_project.remote.model.Servings
import com.example.btl_android_project.remote.model.StaticFood
import com.google.gson.Gson

fun mapToStaticRecipeEntity(recipe: StaticRecipe): StaticRecipesEntity {
    val gson = Gson()

    return StaticRecipesEntity(
        recipeId = recipe.recipeId ?: 0,
        cookingTimeMin = recipe.cookingTimeMin,
        directions = gson.toJson(recipe.directions),
        gramsPerPortion = recipe.gramsPerPortion,
        ingredients = gson.toJson(recipe.ingredients),
        numberOfServings = recipe.numberOfServings,
        preparationTimeMin = recipe.preparationTimeMin,
        rating = recipe.rating,
        recipeCategories = gson.toJson(recipe.recipeCategories),
        recipeDescription = recipe.recipeDescription,
        recipeImages = gson.toJson(recipe.recipeImages),
        recipeName = recipe.recipeName,
        recipeTypes = gson.toJson(recipe.recipeTypes),
        recipeUrl = recipe.recipeUrl,
        servingSizes = gson.toJson(recipe.servingSizes)
    )
}

fun mapToStaticRecipe(entity: StaticRecipesEntity): StaticRecipe {
    val gson = Gson()

    return StaticRecipe(
        recipeId = entity.recipeId,
        cookingTimeMin = entity.cookingTimeMin,
        directions = if (entity.directions != null)
            gson.fromJson(entity.directions, Directions::class.java) else null,
        gramsPerPortion = entity.gramsPerPortion,
        ingredients = if (entity.ingredients != null)
            gson.fromJson(entity.ingredients, Ingredients::class.java) else null,
        numberOfServings = entity.numberOfServings,
        preparationTimeMin = entity.preparationTimeMin,
        rating = entity.rating,
        recipeCategories = if (entity.recipeCategories != null)
            gson.fromJson(entity.recipeCategories, RecipeCategories::class.java) else null,
        recipeDescription = entity.recipeDescription,
        recipeImages = if (entity.recipeImages != null)
            gson.fromJson(entity.recipeImages, RecipeImages::class.java) else null,
        recipeName = entity.recipeName,
        recipeTypes = if (entity.recipeTypes != null)
            gson.fromJson(entity.recipeTypes, RecipeTypes::class.java) else null,
        recipeUrl = entity.recipeUrl,
        servingSizes = if (entity.servingSizes != null)
            gson.fromJson(entity.servingSizes, ServingSizes::class.java) else null
    )
}

fun mapToStaticRecipeList(entityList: List<StaticRecipesEntity>): List<StaticRecipe> {
    return entityList.map { mapToStaticRecipe(it) }
}

fun mapToStaticRecipeEntityList(recipeList: List<StaticRecipe>): List<StaticRecipesEntity> {
    return recipeList.map { mapToStaticRecipeEntity(it) }
}

fun mapToStaticFoodEntity(staticFood: StaticFood): StaticFoodEntity {
    val gson = Gson()

    return StaticFoodEntity(
        foodId = staticFood.foodId ?: 0,
        foodName = staticFood.foodName,
        foodType = staticFood.foodType,
        foodUrl = staticFood.foodUrl,
        servings = gson.toJson(staticFood.servings)
    )
}

fun mapToStaticFood(entity: StaticFoodEntity): StaticFood {
    val gson = Gson()

    return StaticFood(
        foodId = entity.foodId,
        foodName = entity.foodName ?: "",
        foodType = entity.foodType ?: "",
        foodUrl = entity.foodUrl ?: "",
        servings = entity.servings?.let { gson.fromJson(it, Servings::class.java) } ?: Servings(emptyList())
    )
}

fun mapToStaticFoodList(entityList: List<StaticFoodEntity>): List<StaticFood> {
    return entityList.map { mapToStaticFood(it) }
}

fun mapToStaticFoodEntityList(staticFoodList: List<StaticFood>): List<StaticFoodEntity> {
    return staticFoodList.map { mapToStaticFoodEntity(it) }
}