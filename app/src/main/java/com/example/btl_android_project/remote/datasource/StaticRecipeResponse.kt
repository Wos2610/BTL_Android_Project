package com.example.btl_android_project.remote.datasource

import com.example.btl_android_project.entity.StaticRecipe
import com.google.gson.annotations.SerializedName

data class StaticRecipeResponse(
    @SerializedName("recipe")
    val recipe: StaticRecipe,
)