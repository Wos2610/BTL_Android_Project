package com.example.btl_android_project.local
import androidx.room.TypeConverter
import com.example.btl_android_project.local.entity.Nutrition
import com.example.btl_android_project.local.entity.RecipeIngredient
import com.example.btl_android_project.local.entity.StaticRecipeIngredient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromNutritionList(value: List<Nutrition>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toNutritionList(value: String): List<Nutrition> {
        val listType = object : TypeToken<List<Nutrition>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun toString(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromIngredientList(value: List<StaticRecipeIngredient>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toIngredientList(value: String): List<StaticRecipeIngredient> {
        val listType = object : TypeToken<List<StaticRecipeIngredient>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromRecipeIngredientList(value: List<RecipeIngredient>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toRecipeIngredientList(value: String): List<RecipeIngredient> {
        val listType = object : TypeToken<List<RecipeIngredient>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
