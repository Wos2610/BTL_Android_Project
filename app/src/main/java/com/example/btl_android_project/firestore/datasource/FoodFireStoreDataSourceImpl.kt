package com.example.btl_android_project.firestore.datasource

import android.util.Log
import com.example.btl_android_project.local.entity.Food
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FoodFireStoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
){
    suspend fun addFood(food: Food): String = suspendCoroutine { continuation ->
        Log.d("FoodFireStoreDataSourceImpl", "Adding food: ${food.name}")

        val docRef = firestore.collection(FOODS_COLLECTION).document()

        val newId = docRef.id
        val updatedFood = food.copy(id = newId)

        docRef.set(updatedFood)
            .addOnSuccessListener {
                Timber.d("Food ${updatedFood.name} added successfully with ID: $newId")
                continuation.resume(newId)
            }
            .addOnFailureListener { e ->
                Timber.e("Failed to add food ${updatedFood.name}: ${e.message}")
                continuation.resumeWithException(e)
            }
    }

    suspend fun updateFood(food: Food) {
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

    suspend fun deleteFood(foodId: String) {
        try {
            firestore.collection(FOODS_COLLECTION).document(foodId).delete().await()
            Timber.d("Food deleted with ID: $foodId")
        } catch (e: Exception) {
            Timber.e("Error deleting food: ${e.message}")
            throw e
        }
    }

    suspend fun getFoodById(foodId: String): Food? {
        return try {
            val document = firestore.collection(FOODS_COLLECTION).document(foodId).get().await()
            document.toObject(Food::class.java)
        } catch (e: Exception) {
            Timber.e("Error getting food by ID: ${e.message}")
            null
        }
    }

    suspend fun getAllFoodsByUser(userId: String): List<Food> {
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

    suspend fun searchFoods(query: String, userId: String): List<Food> {
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