package com.example.btl_android_project.remote.di

import com.example.btl_android_project.entity.Direction
import com.example.btl_android_project.entity.Ingredient
import com.example.btl_android_project.entity.RecipeCategory
import com.example.btl_android_project.entity.ServingDetails
import com.example.btl_android_project.entity.StaticRecipe
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import kotlin.jvm.javaClass

class GsonConverters {
    companion object {
        val gson: Gson = GsonBuilder()
            .setLenient()
            .registerTypeAdapter(
                object : TypeToken<List<String>>() {}.type, 
                createListTypeAdapter<String>()
            )
            .registerTypeAdapter(
                object : TypeToken<List<RecipeCategory>>() {}.type, 
                createListTypeAdapter<RecipeCategory>()
            )
            .registerTypeAdapter(
                object : TypeToken<List<Ingredient>>() {}.type, 
                createListTypeAdapter<Ingredient>()
            )
            .registerTypeAdapter(
                object : TypeToken<List<Direction>>() {}.type, 
                createListTypeAdapter<Direction>()
            )
            .registerTypeAdapter(
                object : TypeToken<List<String>>() {}.type, 
                createListTypeAdapter<String>()
            )
            .create()

        // Generic type adapter creator for lists
        private inline fun <reified T> createListTypeAdapter(): TypeAdapter<List<T>> {
            return object : TypeAdapter<List<T>>() {
                override fun write(out: JsonWriter, value: List<T>?) {
                    if (value == null) {
                        out.nullValue()
                        return
                    }
                    out.beginArray()
                    value.forEach { item ->
                        gson.toJson(item, (item as Any).javaClass, out)
                    }
                    out.endArray()
                }

                override fun read(input: JsonReader): List<T>? {
                    val list = mutableListOf<T>()
                    
                    input.beginArray()
                    while (input.hasNext()) {
                        val item = gson.fromJson<T>(input, T::class.java)
                        list.add(item)
                    }
                    input.endArray()
                    
                    return list
                }
            }
        }

        // Extension functions for easy conversion
        fun StaticRecipe.convertRecipeTypes(): List<String> {
            val type = object : TypeToken<List<String>>() {}.type
            return gson.fromJson(recipeTypes, type)
        }

        fun StaticRecipe.convertRecipeCategories(): List<RecipeCategory> {
            val type = object : TypeToken<List<RecipeCategory>>() {}.type
            return gson.fromJson(recipeCategories, type)
        }

        fun StaticRecipe.convertServingDetails(): ServingDetails {
            return gson.fromJson(servingDetails, ServingDetails::class.java)
        }

        fun StaticRecipe.convertIngredients(): List<Ingredient> {
            val type = object : TypeToken<List<Ingredient>>() {}.type
            return gson.fromJson(ingredients, type)
        }

        fun StaticRecipe.convertDirections(): List<Direction> {
            val type = object : TypeToken<List<Direction>>() {}.type
            return gson.fromJson(directions, type)
        }

        fun StaticRecipe.convertRecipeImages(): List<String> {
            val type = object : TypeToken<List<String>>() {}.type
            return gson.fromJson(recipeImages, type)
        }
        // Parsing from JSON string methods
        fun String.parseToStringList(): List<String> {
            val type = object : TypeToken<List<String>>() {}.type
            return gson.fromJson(this, type)
        }

        fun String.parseToRecipeCategories(): List<RecipeCategory> {
            val type = object : TypeToken<List<RecipeCategory>>() {}.type
            return gson.fromJson(this, type)
        }

        fun String.parseToIngredients(): List<Ingredient> {
            val type = object : TypeToken<List<Ingredient>>() {}.type
            return gson.fromJson(this, type)
        }

        fun String.parseToDirections(): List<Direction> {
            val type = object : TypeToken<List<Direction>>() {}.type
            return gson.fromJson(this, type)
        }

        fun String.parseToServingDetails(): ServingDetails {
            return gson.fromJson(this, ServingDetails::class.java)
        }
    }
}