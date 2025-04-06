package com.example.btl_android_project.firestore.datasource

import com.example.btl_android_project.firestore.domain.FoodFireStoreDataSource
import com.example.btl_android_project.local.entity.Food
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class FoodFireStoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FoodFireStoreDataSource {

    override suspend fun addFood(food: Food): String {
        try {
            val foodRef = if (food.id != 0) {
                firestore.collection(FOODS_COLLECTION).document(food.id.toString())
            } else {
                firestore.collection(FOODS_COLLECTION).document()
            }

            val foodWithId = if (food.id == 0) {
                food.copy(id = foodRef.id.toInt())
            } else {
                food
            }

            foodRef.set(foodWithId).await()
            Timber.d("Food added with ID: ${foodRef.id}")
            return foodRef.id
        } catch (e: Exception) {
            Timber.e("Error adding food: ${e.message}")
            throw e
        }
    }

    override suspend fun updateFood(food: Food) {
        try {
            val foodRef = firestore.collection(FOODS_COLLECTION).document(food.id.toString())
            val updatedFood = food.copy(updatedAt = System.currentTimeMillis())
            foodRef.set(updatedFood).await()
            Timber.d("Food updated with ID: ${food.id}")
        } catch (e: Exception) {
            Timber.e("Error updating food: ${e.message}")
            throw e
        }
    }

    override suspend fun deleteFood(foodId: String) {
        try {
            firestore.collection(FOODS_COLLECTION).document(foodId).delete().await()
            Timber.d("Food deleted with ID: $foodId")
        } catch (e: Exception) {
            Timber.e("Error deleting food: ${e.message}")
            throw e
        }
    }

    override suspend fun getFoodById(foodId: String): Food? {
        return try {
            val document = firestore.collection(FOODS_COLLECTION).document(foodId).get().await()
            document.toObject(Food::class.java)
        } catch (e: Exception) {
            Timber.e("Error getting food by ID: ${e.message}")
            null
        }
    }

    override suspend fun getAllFoodsByUser(userId: Int): List<Food> {
        return try {
            val result = firestore.collection(FOODS_COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .await()
            result.toObjects(Food::class.java)
        } catch (e: Exception) {
            Timber.e("Error getting foods by user ID: ${e.message}")
            emptyList()
        }
    }

    override suspend fun searchFoods(query: String, userId: Int): List<Food> {
        return try {
            // Firestore doesn't support "LIKE" queries directly, use range queries
            val result = firestore.collection(FOODS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + "\uf8ff")
                .get()
                .await()
            result.toObjects(Food::class.java)
        } catch (e: Exception) {
            Timber.e("Error searching foods: ${e.message}")
            emptyList()
        }
    }

    companion object {
        private const val FOODS_COLLECTION = "foods"
    }
}