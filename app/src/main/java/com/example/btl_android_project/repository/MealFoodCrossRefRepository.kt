package com.example.btl_android_project.repository

import com.example.btl_android_project.firestore.domain.MealFoodCrossRefFireStoreDataSource
import com.example.btl_android_project.local.dao.MealFoodCrossRefDao
import com.example.btl_android_project.utils.mapToStaticFoodEntityList
import com.example.btl_android_project.utils.mapToStaticFoodList
import timber.log.Timber
import javax.inject.Inject
import com.example.btl_android_project.local.entity.MealFoodCrossRef

class MealFoodCrossRefRepository @Inject constructor(
    private val mealFoodCrossRefDao: MealFoodCrossRefDao,
    private val mealFoodCrossRefFireStoreDataSource: MealFoodCrossRefFireStoreDataSource,
) {
    suspend fun pushToFireStore(mealFoodCrossRefs: MealFoodCrossRef) {
        Timber.d("Pushing meal food cross ref to Firestore with ID: ${mealFoodCrossRefs.mealId} and food ID: ${mealFoodCrossRefs.foodId}")
        mealFoodCrossRefFireStoreDataSource.insertMealFoodCrossRef(mealFoodCrossRefs)
    }

    suspend fun pullFromFireStore(userId: Int = 0) {
        Timber.d("Pulling meal food cross refs from Firestore")
        val foods = mealFoodCrossRefFireStoreDataSource.getAllMealFoodCrossRefsByUser(
            userId = userId
        )
        Timber.d("Pulled ${foods.size} foods from Firestore")
        mealFoodCrossRefDao.insertAllMealFoodCrossRefs(foods)
    }

    suspend fun insertMealFoodCrossRef(mealFoodCrossRef: MealFoodCrossRef) {
        Timber.d("Inserting meal food cross ref with ID: ${mealFoodCrossRef.mealId} and food ID: ${mealFoodCrossRef.foodId}")
        mealFoodCrossRefDao.insertMealFoodCrossRef(mealFoodCrossRef)
        pushToFireStore(mealFoodCrossRef)
    }
}