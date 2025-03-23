package com.example.btl_android_project.repository

import android.content.Context
import androidx.room.Room
import com.example.btl_android_project.local.AppDatabase
import com.example.btl_android_project.entity.StaticRecipeIngredient
import com.example.btl_android_project.remote.domain.StaticRecipeIngredientRemoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber
import javax.inject.Inject

class StaticRecipeIngredientRepository @Inject constructor(
    private val db: AppDatabase,
    private val staticRecipeIngredientRemoteDataSource: StaticRecipeIngredientRemoteDataSource
) {

    val staticRecipeIngredientDao = db.staticRecipeIngredientDao()

//    private val db = FirebaseFirestore.getInstance()

    fun addAllRecipeIngredients(recipeIngredients: List<StaticRecipeIngredient>) {
//        val batch = db.batch()
//
//        recipeIngredients.forEachIndexed { index, ingredient ->
//            val docRef = db.collection("recipe_ingredients").document()
//            batch.set(docRef, ingredient)
//
//            if (index > 0 && index % 500 == 0) {
//                batch.commit().addOnSuccessListener {
//                    Timber.d("Batch committed successfully")
//                }.addOnFailureListener {
//                    Timber.e("Batch commit failed: ${it.message}")
//                }
//            }
//        }
//
//        batch.commit().addOnSuccessListener {
//            Timber.d("Final batch committed successfully")
//        }.addOnFailureListener {
//            Timber.e("Final batch commit failed: ${it.message}")
//        }
    }

    suspend fun getStaticRecipeIngredients() = staticRecipeIngredientRemoteDataSource.getStaticRecipeIngredients()
}