package com.example.btl_android_project.presentation.log_exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.auth.FirebaseAuthDataSource
import com.example.btl_android_project.local.entity.Exercise
import com.example.btl_android_project.repository.ExercisesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NewExerciseViewModel @Inject constructor(
    private val exercisesRepository: ExercisesRepository,
    private val firebaseAuthDataSource: FirebaseAuthDataSource
) : ViewModel() {

    private val _createStatus = MutableLiveData<Boolean>()
    val createStatus: LiveData<Boolean> = _createStatus

    private val userId = firebaseAuthDataSource.getCurrentUserId()

    fun createExercise(description: String, minutes: Int, calories: Float) {
        viewModelScope.launch {
            try {
                val exercise = Exercise(
                    description = description,
                    minutesPerformed = minutes,
                    caloriesBurned = calories,
                    userId = userId.toString()
                )
                exercisesRepository.insertExercise(exercise)
                _createStatus.value = true
            } catch (e: Exception) {
                Timber.e("Failed to create exercise: ${e.message}")
                _createStatus.value = false
            }
        }
    }
}
