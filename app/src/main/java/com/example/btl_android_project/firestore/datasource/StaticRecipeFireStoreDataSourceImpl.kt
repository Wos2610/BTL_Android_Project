package com.example.btl_android_project.firestore.datasource

import com.example.btl_android_project.firestore.domain.StaticRecipeFireStoreDataSource
import com.example.btl_android_project.remote.model.StaticRecipe
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class StaticRecipeFireStoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : StaticRecipeFireStoreDataSource {
    override suspend fun addAllRecipes(recipes: List<StaticRecipe>) {
        var batch = firestore.batch()
        val tasks = mutableListOf<Task<Void>>()

        recipes.forEachIndexed { index, recipe ->
            val docRef = firestore.collection("static_recipes").document(recipe.recipeId.toString())
            batch.set(docRef, recipe)

            if (index > 0 && index % 500 == 0) {
                val commitTask = batch.commit()
                tasks.add(commitTask)

                batch = firestore.batch()
            }
        }

        if (recipes.size % 500 != 0) {
            val commitTask = batch.commit()
            tasks.add(commitTask)
        }

        try {
            Tasks.whenAll(tasks).await()
            Timber.d("All batches committed successfully")
        } catch (e: Exception) {
            Timber.e("Batch commit failed: ${e.message}")
        }
    }
}