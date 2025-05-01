package com.example.btl_android_project.firestore.datasource

import com.example.btl_android_project.local.entity.Meal
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class MealFireStoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
){

    companion object {
        private const val MEALS_COLLECTION = "meals"
        private const val MAX_BATCH_SIZE = 500
    }

    fun addAllMeals(meals: List<Meal>) {
        val batchedMeals = meals.chunked(MAX_BATCH_SIZE)

        batchedMeals.forEachIndexed { batchIndex, batchList ->
            val batch = firestore.batch()

            batchList.forEach { meal ->
                val docRef = firestore.collection(MEALS_COLLECTION).document(meal.id.toString())
                batch.set(docRef, meal)
            }

            batch.commit().addOnSuccessListener {
                Timber.Forest.d("Batch #$batchIndex committed successfully")
            }.addOnFailureListener {
                Timber.Forest.e("Batch #$batchIndex commit failed: ${it.message}")
            }
        }
    }

    fun addMeal(meal: Meal) {
        val docRef = firestore.collection(MEALS_COLLECTION).document(meal.id.toString())
        docRef.set(meal)
            .addOnSuccessListener {
                Timber.Forest.d("Meal ${meal.name} added successfully")
            }
            .addOnFailureListener { e ->
                Timber.Forest.e("Failed to add Meal ${meal.name}: ${e.message}")
            }
    }

    fun deleteMeal(mealId: Int) {
        val docRef = firestore.collection(MEALS_COLLECTION).document(mealId.toString())
        docRef.delete()
            .addOnSuccessListener {
                Timber.Forest.d("Meal with ID $mealId deleted successfully")
            }
            .addOnFailureListener { e ->
                Timber.Forest.e("Failed to delete Meal with ID $mealId: ${e.message}")
            }
    }

    suspend fun getAllMealsByUser(userId: Int): List<Meal> {
        val snapshot = firestore.collection(MEALS_COLLECTION)
            .whereEqualTo("userId", userId)
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(Meal::class.java) }
    }

    suspend fun updateMeal(meal: Meal) {
        val docRef = firestore.collection(MEALS_COLLECTION).document(meal.id.toString())
        docRef.set(meal)
            .addOnSuccessListener {
                Timber.Forest.d("Meal ${meal.name} updated successfully")
            }
            .addOnFailureListener { e ->
                Timber.Forest.e("Failed to update Meal ${meal.name}: ${e.message}")
            }
    }
}
