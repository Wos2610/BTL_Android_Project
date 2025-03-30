package com.example.btl_android_project.local

import androidx.room.TypeConverter
import com.example.btl_android_project.remote.model.Directions
import com.example.btl_android_project.remote.model.Ingredients
import com.example.btl_android_project.remote.model.RecipeCategories
import com.example.btl_android_project.remote.model.RecipeImages
import com.example.btl_android_project.remote.model.RecipeTypes
import com.example.btl_android_project.remote.model.ServingSizes
import com.google.gson.Gson

class RecipeTypeConverters {
    private val gson = Gson()

    // Các hàm để chuyển đổi từ đối tượng phức tạp sang chuỗi JSON khi lưu vào database
    @TypeConverter
    fun fromDirectionsToString(directions: Directions?): String? {
        return gson.toJson(directions)
    }

    @TypeConverter
    fun fromStringToDirections(value: String?): Directions? {
        if (value == null) return null
        return gson.fromJson(value, Directions::class.java)
    }

    @TypeConverter
    fun fromIngredientsToString(ingredients: Ingredients?): String? {
        return gson.toJson(ingredients)
    }

    @TypeConverter
    fun fromStringToIngredients(value: String?): Ingredients? {
        if (value == null) return null
        return gson.fromJson(value, Ingredients::class.java)
    }

    @TypeConverter
    fun fromRecipeCategoriesToString(recipeCategories: RecipeCategories?): String? {
        return gson.toJson(recipeCategories)
    }

    @TypeConverter
    fun fromStringToRecipeCategories(value: String?): RecipeCategories? {
        if (value == null) return null
        return gson.fromJson(value, RecipeCategories::class.java)
    }

    @TypeConverter
    fun fromRecipeImagesToString(recipeImages: RecipeImages?): String? {
        return gson.toJson(recipeImages)
    }

    @TypeConverter
    fun fromStringToRecipeImages(value: String?): RecipeImages? {
        if (value == null) return null
        return gson.fromJson(value, RecipeImages::class.java)
    }

    @TypeConverter
    fun fromRecipeTypesToString(recipeTypes: RecipeTypes?): String? {
        return gson.toJson(recipeTypes)
    }

    @TypeConverter
    fun fromStringToRecipeTypes(value: String?): RecipeTypes? {
        if (value == null) return null
        return gson.fromJson(value, RecipeTypes::class.java)
    }

    @TypeConverter
    fun fromServingSizesToString(servingSizes: ServingSizes?): String? {
        return gson.toJson(servingSizes)
    }

    @TypeConverter
    fun fromStringToServingSizes(value: String?): ServingSizes? {
        if (value == null) return null
        return gson.fromJson(value, ServingSizes::class.java)
    }
}