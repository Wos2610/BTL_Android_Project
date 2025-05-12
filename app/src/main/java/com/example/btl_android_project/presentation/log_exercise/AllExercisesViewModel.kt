package com.example.btl_android_project.presentation.log_exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.local.entity.Exercise
import com.example.btl_android_project.repository.ExercisesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AllExercisesViewModel @Inject constructor(
    private val exercisesRepository: ExercisesRepository
) : ViewModel() {

    private val _exercises = MutableLiveData<List<Exercise>>()
    val exercises: LiveData<List<Exercise>> = _exercises

    fun loadAllExercises() {
        viewModelScope.launch {
            try {
                exercisesRepository.getAllExercises().collect { exercises ->
                    Timber.d("Loaded exercises: $exercises")
                    _exercises.value = exercises
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load log exercises: ${e.message}")
                _exercises.value = emptyList()
            }
        }
    }

}