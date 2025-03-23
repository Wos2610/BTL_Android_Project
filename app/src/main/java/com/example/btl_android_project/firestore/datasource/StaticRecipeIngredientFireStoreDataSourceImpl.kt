package com.example.btl_android_project.firestore.datasource

import com.example.btl_android_project.entity.StaticRecipeIngredient
import com.example.btl_android_project.firestore.domain.StaticRecipeIngredientFireStoreDataSource
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber
import javax.inject.Inject

class StaticRecipeIngredientFireStoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : StaticRecipeIngredientFireStoreDataSource {
    override fun addAllRecipeIngredients(recipeIngredients: List<StaticRecipeIngredient>) {
        val batch = firestore.batch()

        recipeIngredients.forEachIndexed { index, ingredient ->
            val docRef = firestore.collection("static_recipe_ingredients").document(ingredient.fdcId.toString())
            batch.set(docRef, ingredient)

            if (index > 0 && index % 500 == 0) {
                batch.commit().addOnSuccessListener {
                    Timber.Forest.d("Batch committed successfully")
                }.addOnFailureListener {
                    Timber.Forest.e("Batch commit failed: ${it.message}")
                }
            }
        }

        batch.commit().addOnSuccessListener {
            Timber.Forest.d("Final batch committed successfully")
        }.addOnFailureListener {
            Timber.Forest.e("Final batch commit failed: ${it.message}")
        }
    }
}