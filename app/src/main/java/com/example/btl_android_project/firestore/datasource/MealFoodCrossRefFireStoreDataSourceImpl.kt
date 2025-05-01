package com.example.btl_android_project.firestore.datasource

import com.example.btl_android_project.local.entity.MealFoodCrossRef
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class MealFoodCrossRefFireStoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
){

    suspend fun insertMealFoodCrossRef(food: MealFoodCrossRef) {
        val docId = "${food.mealId}_${food.foodId}"
        val docRef = firestore.collection(MEAL_FOOD_CROSS_REF_COLLECTION).document(docId)

        docRef.set(food)
            .addOnSuccessListener {
                Timber.d("MealFoodCrossRef added: mealId=${food.mealId}, foodId=${food.foodId}")
            }
            .addOnFailureListener { e ->
                Timber.e(e, "Failed to add MealFoodCrossRef: mealId=${food.mealId}, foodId=${food.foodId}")
            }
    }

    suspend fun updateMealFoodCrossRef(food: MealFoodCrossRef) {
        TODO("Not yet implemented")
    }


    suspend fun deleteMealFoodCrossRef(foodId: String) {
        // Query for all documents where recipeId matches the given value
        val snapshot = firestore.collection(MEAL_FOOD_CROSS_REF_COLLECTION)
            .whereEqualTo("recipeId", foodId)
            .get()
            .await()

        // Delete each matching document
        val batch = firestore.batch()
        snapshot.documents.forEach { document ->
            batch.delete(document.reference)
        }

        // Commit the batch
        if (snapshot.documents.isNotEmpty()) {
            batch.commit().await()
        }
    }

    suspend fun getMealFoodCrossRefById(foodId: String): MealFoodCrossRef? {
        TODO("Not yet implemented")
    }

    suspend fun getAllMealFoodCrossRefsByUser(userId: Int): List<MealFoodCrossRef> {
        val snapshot = firestore.collection(MEAL_FOOD_CROSS_REF_COLLECTION)
            .whereEqualTo("userId", userId)
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(MealFoodCrossRef::class.java) }
    }


    suspend fun searchMealFoodCrossRefs(
        query: String,
        userId: Int
    ): List<MealFoodCrossRef> {
        TODO("Not yet implemented")
    }

    suspend fun deleteMealFoodCrossRefByMealId(mealId: Int) {
        // Query for all documents where recipeId matches the given value
        val snapshot = firestore.collection(MEAL_FOOD_CROSS_REF_COLLECTION)
            .whereEqualTo("mealId", mealId)
            .get()
            .await()

        // Delete each matching document
        val batch = firestore.batch()
        snapshot.documents.forEach { document ->
            batch.delete(document.reference)
        }

        // Commit the batch
        if (snapshot.documents.isNotEmpty()) {
            batch.commit().await()
        }
    }

    companion object {
        private const val MEAL_FOOD_CROSS_REF_COLLECTION = "meal_food_cross_ref"
        private const val MAX_BATCH_SIZE = 500
    }
}