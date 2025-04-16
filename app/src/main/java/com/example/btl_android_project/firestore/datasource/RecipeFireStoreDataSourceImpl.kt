package com.example.btl_android_project.firestore.datasource

import com.example.btl_android_project.firestore.domain.RecipeFireStoreDataSource
import com.example.btl_android_project.local.entity.Recipe
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber
import javax.inject.Inject

class RecipeFireStoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RecipeFireStoreDataSource {

    companion object {
        private const val RECIPES_COLLECTION = "recipes"
        private const val MAX_BATCH_SIZE = 500
    }

    override fun addAllRecipes(recipes: List<Recipe>) {
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

    override fun addRecipe(recipe: Recipe) {
        val docRef = firestore.collection(RECIPES_COLLECTION).document(recipe.id.toString())
        docRef.set(recipe)
            .addOnSuccessListener {
                Timber.Forest.d("Recipe ${recipe.name} added successfully")
            }
            .addOnFailureListener { e ->
                Timber.Forest.e("Failed to add recipe ${recipe.name}: ${e.message}")
            }
    }

}
