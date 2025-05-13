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


    suspend fun deleteMealFoodCrossRef(foodId: String) {
        val snapshot = firestore.collection(MEAL_FOOD_CROSS_REF_COLLECTION)
            .whereEqualTo("recipeId", foodId)
            .get()
            .await()

        val batch = firestore.batch()
        snapshot.documents.forEach { document ->
            batch.delete(document.reference)
        }

        if (snapshot.documents.isNotEmpty()) {
            batch.commit().await()
        }
    }

    suspend fun getMealFoodCrossRefById(foodId: String): MealFoodCrossRef? {
        TODO("Not yet implemented")
    }

    suspend fun getAllMealFoodCrossRefsByUser(userId: String): List<MealFoodCrossRef> {
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

    suspend fun deleteMealFoodCrossRefByMealId(mealId: String) {
        val snapshot = firestore.collection(MEAL_FOOD_CROSS_REF_COLLECTION)
            .whereEqualTo("mealId", mealId)
            .get()
            .await()

        val batch = firestore.batch()
        snapshot.documents.forEach { document ->
            batch.delete(document.reference)
        }

        if (snapshot.documents.isNotEmpty()) {
            batch.commit().await()
        }
    }

    suspend fun getMealFoodCrossRefByMealId(mealId: String): List<MealFoodCrossRef> {
        val snapshot = firestore.collection(MEAL_FOOD_CROSS_REF_COLLECTION)
            .whereEqualTo("mealId", mealId)
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(MealFoodCrossRef::class.java) }
    }

    suspend fun getAllByMealIds(mealIds: List<String>): List<MealFoodCrossRef> {
        if (mealIds.isEmpty()) {
            return emptyList()
        }

        val result = mutableListOf<MealFoodCrossRef>()

        // Xử lý theo batch để tránh vượt quá giới hạn truy vấn Firestore
        val batchSize = 10 // Firestore giới hạn tối đa 10 phần tử cho mệnh đề IN
        val startTime = System.currentTimeMillis()

        mealIds.chunked(batchSize).forEach { mealIdBatch ->
            Timber.d("Fetching food cross refs for meal batch size: ${mealIdBatch.size}")

            try {
                val snapshot = firestore.collection(MEAL_FOOD_CROSS_REF_COLLECTION)
                    .whereIn("mealId", mealIdBatch)
                    .get()
                    .await()

                val batchResults = snapshot.documents.mapNotNull {
                    it.toObject(MealFoodCrossRef::class.java)
                }

                result.addAll(batchResults)
                Timber.d("Fetched ${batchResults.size} food cross refs for this batch")
            } catch (e: Exception) {
                Timber.e(e, "Error fetching food cross refs for meal batch: ${e.message}")
            }
        }

        val elapsedTime = System.currentTimeMillis() - startTime
        Timber.d("Total food cross refs fetched: ${result.size} in ${elapsedTime}ms")
        return result
    }

    companion object {
        private const val MEAL_FOOD_CROSS_REF_COLLECTION = "meal_food_cross_ref"
        private const val MAX_BATCH_SIZE = 500
    }
}