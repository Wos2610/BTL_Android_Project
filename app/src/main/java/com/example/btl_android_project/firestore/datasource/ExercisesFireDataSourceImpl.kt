package com.example.btl_android_project.firestore.datasource

import com.example.btl_android_project.local.entity.Exercise
import com.example.btl_android_project.local.entity.LogWeight
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class ExercisesFireDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
){
    private val collection = firestore.collection("exercises")

    suspend fun insertExercise(exercise: Exercise): String {
        val docRef = collection.document()
        val newLog = exercise.copy(id = docRef.id)
        docRef.set(newLog).await()
        return docRef.id
    }

    suspend fun updateExercise(exercise: Exercise) {
        collection.document(exercise.id).set(exercise).await()
    }

    suspend fun deleteExercise(id: String) {
        collection.document(id).delete().await()
    }

    suspend fun getAllExercisesByUser(userId: String): List<Exercise> {
        val snapshot = collection
            .whereEqualTo("userId", userId)
            .get()
            .await()
        return snapshot.toObjects(Exercise::class.java)
    }

    suspend fun getAllExercises(): Flow<List<Exercise>> {
        return flow {
            val snapshot = collection.get().await()
            emit(snapshot.toObjects(Exercise::class.java))
        }
    }



    suspend fun getExerciseById(id: String): Exercise? {
        val doc = collection.document(id).get().await()
        return if (doc.exists()) doc.toObject(Exercise::class.java) else null
    }
}
