package com.example.btl_android_project.presentation.diary

data class MealSection(
    val title: String,
    val calories: Int,
    val items: List<MealItem>
)