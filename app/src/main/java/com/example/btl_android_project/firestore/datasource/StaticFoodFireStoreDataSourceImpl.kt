package com.example.btl_android_project.firestore.datasource
import com.example.btl_android_project.firestore.domain.StaticFoodFireStoreDataSource
import com.example.btl_android_project.remote.model.StaticFood
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class StaticFoodFireStoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : StaticFoodFireStoreDataSource {
    override suspend fun addAllFoods(foods: List<StaticFood>) {
        var batch = firestore.batch()
        val tasks = mutableListOf<Task<Void>>()

        foods.forEachIndexed { index, food ->
            val docRef = firestore.collection("static_foods").document(food.foodId.toString())
            batch.set(docRef, food)

            if (index > 0 && index % 500 == 0) {
                val commitTask = batch.commit()
                tasks.add(commitTask)

                batch = firestore.batch()
            }
        }

        if (foods.size % 500 != 0) {
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