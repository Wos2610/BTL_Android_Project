package com.example.btl_android_project.repository

import com.example.btl_android_project.firestore.domain.FoodFireStoreDataSource
import com.example.btl_android_project.local.dao.FoodDao
import com.example.btl_android_project.local.entity.Food
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber
import javax.inject.Inject

class FoodRepository @Inject constructor(
    private val foodDao: FoodDao,
    private val foodFireStoreDataSource: FoodFireStoreDataSource
) {
    // Local operations
    suspend fun insertFood(food: Food): Long {
        return try {
            // First insert to local database
            val localId = foodDao.insertFood(food)

            // Then sync to Firestore
            val firestoreId = foodFireStoreDataSource.addFood(food.copy(id = localId.toInt()))

            Timber.d("Food inserted with local ID: $localId and Firestore ID: $firestoreId")
            localId
        } catch (e: Exception) {
            Timber.e("Error inserting food: ${e.message}")
            throw e
        }
    }

    suspend fun updateFood(food: Food) {
        try {
            // Update local database
            foodDao.updateFood(food)

            // Sync to Firestore
            foodFireStoreDataSource.updateFood(food)

            Timber.d("Food updated with ID: ${food.id}")
        } catch (e: Exception) {
            Timber.e("Error updating food: ${e.message}")
            throw e
        }
    }

    suspend fun deleteFood(food: Food) {
        try {
            // Delete from local database
            foodDao.deleteFood(food)

            // Sync deletion to Firestore
            foodFireStoreDataSource.deleteFood(food.id.toString())

            Timber.d("Food deleted with ID: ${food.id}")
        } catch (e: Exception) {
            Timber.e("Error deleting food: ${e.message}")
            throw e
        }
    }

    suspend fun getFoodById(foodId: Int): Food? {
        return try {
            // Try to get from local database first
            val localFood = foodDao.getFoodById(foodId)

            if (localFood != null) {
                localFood
            } else {
                // If not found locally, try to get from Firestore
                val firestoreFood = foodFireStoreDataSource.getFoodById(foodId.toString())

                // If found in Firestore, insert to local database
                if (firestoreFood != null) {
                    foodDao.insertFood(firestoreFood)
                }

                firestoreFood
            }
        } catch (e: Exception) {
            Timber.e("Error getting food by ID: ${e.message}")
            null
        }
    }

    fun getAllFoodsByUser(userId: Int): Flow<List<Food>> {
        return try {
            foodDao.getAllFoodsByUser(userId)
        } catch (e: Exception) {
            Timber.e("Error getting all foods by user: ${e.message}")
            flowOf(emptyList())
        }
    }

    fun searchFoods(query: String): Flow<List<Food>> {
        return try {
            foodDao.searchFoods(query)
        } catch (e: Exception) {
            Timber.e("Error searching foods: ${e.message}")
            flowOf(emptyList())
        }
    }

    // Sync functions
    suspend fun syncFoodsFromFirestore(userId: Int) {
        try {
            val firestoreFoods = foodFireStoreDataSource.getAllFoodsByUser(userId)
            foodDao.insertAllFoods(firestoreFoods)
            Timber.d("Synced ${firestoreFoods.size} foods from Firestore")
        } catch (e: Exception) {
            Timber.e("Error syncing foods from Firestore: ${e.message}")
        }
    }
}