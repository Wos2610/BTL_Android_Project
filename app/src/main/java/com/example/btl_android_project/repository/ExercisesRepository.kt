package com.example.btl_android_project.repository

import com.example.btl_android_project.firestore.datasource.ExercisesFireDataSourceImpl
import com.example.btl_android_project.local.dao.ExerciseDao
import com.example.btl_android_project.local.entity.Exercise
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber
import javax.inject.Inject

class ExercisesRepository @Inject constructor(
    private val exerciseDao: ExerciseDao,
    private val exercisesFireStoreDataSource: ExercisesFireDataSourceImpl
) {

    suspend fun getAllExercises(): Flow<List<Exercise>> = exercisesFireStoreDataSource.getAllExercises()

    fun getAllExercisesByUser(userId: String): Flow<List<Exercise>> {
        return try {
            exerciseDao.getAllExercisesByUser(userId)
        } catch (e: Exception) {
            Timber.e("Error getting Exercise list: ${e.message}")
            flowOf(emptyList())
        }
    }

    suspend fun syncExercisesFromFirestore(userId: String) {
        try {
            val exercises = exercisesFireStoreDataSource.getAllExercisesByUser(userId)
            exercises.forEach { exerciseDao.insertExercise(it) }
            Timber.d("Synced ${exercises.size} exercise from Firestore")
        } catch (e: Exception) {
            Timber.e("Error syncing exercises: ${e.message}")
        }
    }



    suspend fun insertExercise(exercise: Exercise): String {
        return try {
            val firestoreId = exercisesFireStoreDataSource.insertExercise(exercise)
            val updatedExercise = exercise.copy(id = firestoreId)
            exerciseDao.insertExercise(updatedExercise)

            Timber.d("Exercise inserted with ID: $firestoreId")
            firestoreId
        } catch (e: Exception) {
            Timber.e("Error inserting exercise: ${e.message}")
            throw e
        }
    }


    suspend fun updateExercise(exercise: Exercise) {
        try {
            exerciseDao.updateExercise(exercise)
            exercisesFireStoreDataSource.updateExercise(exercise)

            Timber.d("Exercise updated with ID: ${exercise.id}")
        } catch (e: Exception) {
            Timber.e("Error updating exercise: ${e.message}")
            throw e
        }
    }

    suspend fun deleteExercise(exercise: Exercise) {
        try {
            exerciseDao.deleteExercise(exercise)
            exercisesFireStoreDataSource.deleteExercise(exercise.id)

            Timber.d("Exercise deleted with ID: ${exercise.id}")
        } catch (e: Exception) {
            Timber.e("Error deleting exercise: ${e.message}")
            throw e
        }
    }

    suspend fun getExerciseById(id: String): Exercise? {
        return try {
            exerciseDao.getExerciseById(id)
                ?: exercisesFireStoreDataSource.getExerciseById(id)?.also {
                    exerciseDao.insertExercise(it)
                }
        } catch (e: Exception) {
            Timber.e("Error getting exercise by ID: ${e.message}")
            null
        }
    }

}
