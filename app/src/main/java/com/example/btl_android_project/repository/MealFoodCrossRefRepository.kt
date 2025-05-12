package com.example.btl_android_project.repository

import com.example.btl_android_project.firestore.datasource.MealFoodCrossRefFireStoreDataSourceImpl
import com.example.btl_android_project.local.dao.MealFoodCrossRefDao
import com.example.btl_android_project.local.entity.MealFoodCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class MealFoodCrossRefRepository @Inject constructor(
    private val mealFoodCrossRefDao: MealFoodCrossRefDao,
    private val mealFoodCrossRefFireStoreDataSource: MealFoodCrossRefFireStoreDataSourceImpl,
) {
    suspend fun pushToFireStore(mealFoodCrossRefs: MealFoodCrossRef) {
        Timber.d("Pushing meal food cross ref to Firestore with ID: ${mealFoodCrossRefs.mealId} and food ID: ${mealFoodCrossRefs.foodId}")
        mealFoodCrossRefFireStoreDataSource.insertMealFoodCrossRef(mealFoodCrossRefs)
    }

    suspend fun pullFromFireStore(userId: String) {
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

    suspend fun deleteMealFoodCrossRefByFoodId(foodId: String) {
        Timber.d("Deleting meal food cross ref with food ID: $foodId")
        mealFoodCrossRefDao.deleteMealFoodCrossRefByFoodId(foodId)
        mealFoodCrossRefFireStoreDataSource.deleteMealFoodCrossRef(foodId)
    }

    suspend fun getMealFoodCrossRefById(mealId: String): List<MealFoodCrossRef>? {
        Timber.d("Getting meal food cross ref with ID: $mealId")
        return mealFoodCrossRefDao.getMealFoodCrossRefById(mealId)
    }

    suspend fun deleteMealFoodCrossRefByMealId(mealId: String) {
        Timber.d("Deleting meal food cross ref with meal ID: $mealId")
        mealFoodCrossRefDao.deleteMealFoodCrossRefByMealId(mealId)
        mealFoodCrossRefFireStoreDataSource.deleteMealFoodCrossRefByMealId(mealId)
    }

    suspend fun pullFromFireStoreByMealId(mealId: String) {
        withContext(Dispatchers.IO) {
            Timber.d("Pulling meal food cross ref from Firestore with meal ID: $mealId")
            val foods = mealFoodCrossRefFireStoreDataSource.getMealFoodCrossRefByMealId(mealId)
            Timber.d("Pulled ${foods.size} foods from Firestore")
            if (foods.isNotEmpty()) {
                mealFoodCrossRefDao.deleteAndInsertInTransaction(mealId, foods)
            }
        }
    }

    suspend fun getMealFoodCrossRefByFoodId(foodId: String): List<MealFoodCrossRef>? {
        return withContext(Dispatchers.IO) {
            Timber.d("Getting meal food cross ref with food ID: $foodId")
            mealFoodCrossRefDao.getMealFoodCrossRefByFoodId(foodId)
        }
    }

    suspend fun pullFromFireStoreByMealIds(mealIds: List<String>) {
        withContext(Dispatchers.IO) {
            val allCrossRefs = mealFoodCrossRefFireStoreDataSource.getAllByMealIds(mealIds)
            mealFoodCrossRefDao.deleteAllForMeals(mealIds)
            mealFoodCrossRefDao.insertAll(allCrossRefs)
        }
    }
}