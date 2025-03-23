package com.example.btl_android_project.local
import androidx.room.TypeConverter
import com.example.btl_android_project.entity.Nutrition
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
}
