package com.example.btl_android_project.firestore.datasource

import android.util.Log
import com.example.btl_android_project.local.entity.Recipe
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class RecipeFireStoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
){

    companion object {
        private const val RECIPES_COLLECTION = "recipes"
        private const val MAX_BATCH_SIZE = 500
    }

    fun addAllRecipes(recipes: List<Recipe>) {
        val batchedRecipes = recipes.chunked(MAX_BATCH_SIZE)

        batchedRecipes.forEachIndexed { batchIndex, batchList ->
            val batch = firestore.batch()

            batchList.forEach { recipe ->
                val docRef = firestore.collection(RECIPES_COLLECTION).document(recipe.id.toString())
                batch.set(docRef, recipe)
            }

            batch.commit().addOnSuccessListener {
                Timber.Forest.d("Batch #$batchIndex committed successfully")
            }.addOnFailureListener {
                Timber.Forest.e("Batch #$batchIndex commit failed: ${it.message}")
            }
        }
    }

    fun updateRecipe(recipe: Recipe) {
        Log.d("RecipeFireStoreDataSourceImpl", "Adding recipe: ${recipe.id}")
        val docRef = firestore.collection(RECIPES_COLLECTION).document(recipe.id)
        docRef.set(recipe)
            .addOnSuccessListener {
                Timber.Forest.d("Recipe ${recipe.name} added successfully")
            }
            .addOnFailureListener { e ->
                Timber.Forest.e("Failed to add recipe ${recipe.name}: ${e.message}")
            }
    }

    fun deleteRecipe(recipeId: String) {
        val docRef = firestore.collection(RECIPES_COLLECTION).document(recipeId)
        docRef.delete()
            .addOnSuccessListener {
                Timber.Forest.d("Recipe with ID $recipeId deleted successfully")
            }
            .addOnFailureListener { e ->
                Timber.Forest.e("Failed to delete recipe with ID $recipeId: ${e.message}")
            }
    }

    suspend fun getAllRecipesByUser(userId: String): List<Recipe> {
        return try {
            val result = firestore.collection(RECIPES_COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .await()
            result.toObjects(Recipe::class.java)
        } catch (e: Exception) {
            Timber.e("Error getting foods by user ID: ${e.message}")
            emptyList()
        }
    }

    suspend fun addRecipe(recipe: Recipe): String = suspendCoroutine { continuation ->
        Log.d("RecipeFireStoreDataSourceImpl", "Adding recipe: ${recipe.name}")

        val docRef = firestore.collection(RECIPES_COLLECTION).document()

        val newId = docRef.id
        val updatedRecipe = recipe.copy(id = newId)

        docRef.set(updatedRecipe)
            .addOnSuccessListener {
                Timber.d("Recipe ${updatedRecipe.name} added successfully with ID: $newId")
                continuation.resume(newId)
            }
            .addOnFailureListener { e ->
                Timber.e("Failed to add recipe ${updatedRecipe.name}: ${e.message}")
                continuation.resumeWithException(e)
            }
    }

    suspend fun getRecipeById(recipeId: String): Recipe? {
        return try {
            val result = firestore.collection(RECIPES_COLLECTION)
                .document(recipeId)
                .get()
                .await()
            result.toObject(Recipe::class.java)
        } catch (e: Exception) {
            Timber.e("Error getting recipe by ID: ${e.message}")
            null
        }
    }
}
