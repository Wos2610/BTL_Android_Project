package com.example.btl_android_project.repository

import com.example.btl_android_project.firestore.datasource.MealRecipeCrossRefFireStoreDataSourceImpl
import com.example.btl_android_project.local.dao.MealRecipeCrossRefDao
import com.example.btl_android_project.local.entity.MealRecipeCrossRef
import timber.log.Timber
import javax.inject.Inject

class MealRecipeCrossRefRepository @Inject constructor(
    private val mealRecipeCrossRefDao: MealRecipeCrossRefDao,
    private val mealRecipeCrossRefFireStoreDataSource: MealRecipeCrossRefFireStoreDataSourceImpl,
) {
    suspend fun pushToFireStore(mealRecipeCrossRefs: MealRecipeCrossRef) {
        Timber.d("Pushing meal food cross ref to Firestore with ID: ${mealRecipeCrossRefs.mealId} and food ID: ${mealRecipeCrossRefs.recipeId}")
        mealRecipeCrossRefFireStoreDataSource.insertMealRecipeCrossRef(mealRecipeCrossRefs)
    }

    suspend fun pullFromFireStore(userId: Int = 0) {
        Timber.d("Pulling meal food cross refs from Firestore")
        val foods = mealRecipeCrossRefFireStoreDataSource.getAllMealRecipeCrossRefsByUser(
            userId = userId
        )
        Timber.d("Pulled ${foods.size} foods from Firestore")
        mealRecipeCrossRefDao.insertAllMealRecipeCrossRefs(foods)
    }

    suspend fun insertMealRecipeCrossRef(mealRecipeCrossRef: MealRecipeCrossRef) {
        Timber.d("Inserting meal food cross ref with ID: ${mealRecipeCrossRef.mealId} and food ID: ${mealRecipeCrossRef.recipeId}")
        mealRecipeCrossRefDao.insertMealRecipeCrossRef(mealRecipeCrossRef)
        pushToFireStore(mealRecipeCrossRef)
    }

    suspend fun getMealRecipeCrossRefById(mealId: Int): List<MealRecipeCrossRef>? {
        Timber.d("Getting meal food cross ref with ID: $mealId")
        return mealRecipeCrossRefDao.getMealRecipeCrossRefById(mealId)
    }

    suspend fun deleteMealRecipeCrossRefByMealId(mealId: Int) {
        Timber.d("Deleting meal food cross ref with meal ID: $mealId")
        mealRecipeCrossRefDao.deleteMealRecipeCrossRefByMealId(mealId)
        mealRecipeCrossRefFireStoreDataSource.deleteMealRecipeCrossRefByMealId(mealId)
    }

}