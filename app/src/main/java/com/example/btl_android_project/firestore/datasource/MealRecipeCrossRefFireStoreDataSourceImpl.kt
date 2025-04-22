package com.example.btl_android_project.firestore.datasource

import com.example.btl_android_project.firestore.domain.MealRecipeCrossRefFireStoreDataSource
import com.example.btl_android_project.local.entity.MealRecipeCrossRef
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class MealRecipeCrossRefFireStoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MealRecipeCrossRefFireStoreDataSource {

    override suspend fun insertMealRecipeCrossRef(recipe: MealRecipeCrossRef) {
        val docId = "${recipe.mealId}_${recipe.recipeId}"
        val docRef = firestore.collection(MEAL_RECIPE_CROSS_REF_COLLECTION).document(docId)

        docRef.set(recipe)
            .addOnSuccessListener {
                Timber.d("MealRecipeCrossRef added: mealId=${recipe.mealId}, recipeId=${recipe.recipeId}")
            }

            .addOnFailureListener { e ->
                Timber.e(e, "Failed to add MealRecipeCrossRef: mealId=${recipe.mealId}, recipeId=${recipe.recipeId}")
            }
    }

    override suspend fun updateMealRecipeCrossRef(recipe: MealRecipeCrossRef) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMealRecipeCrossRef(recipeId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getMealRecipeCrossRefById(recipeId: String): MealRecipeCrossRef? {
        TODO("Not yet implemented")
    }

    override suspend fun getAllMealRecipeCrossRefsByUser(userId: Int): List<MealRecipeCrossRef> {
        val snapshot = firestore.collection(MEAL_RECIPE_CROSS_REF_COLLECTION)
            .whereEqualTo("userId", userId)
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(MealRecipeCrossRef::class.java) }
    }


    override suspend fun searchMealRecipeCrossRefs(
        query: String,
        userId: Int
    ): List<MealRecipeCrossRef> {
        TODO("Not yet implemented")
    }

    companion object {
        private const val MEAL_RECIPE_CROSS_REF_COLLECTION = "meal_recipe_cross_ref"
        private const val MAX_BATCH_SIZE = 500
    }
}