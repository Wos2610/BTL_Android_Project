package com.example.btl_android_project.firestore.domain

import com.example.btl_android_project.remote.model.StaticFood

interface StaticFoodFireStoreDataSource {
    suspend fun addAllFoods(foods: List<StaticFood>)
}