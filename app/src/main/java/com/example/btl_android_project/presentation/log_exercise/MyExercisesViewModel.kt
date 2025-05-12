package com.example.btl_android_project.presentation.log_exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.auth.FirebaseAuthDataSource
import com.example.btl_android_project.local.entity.DiaryExerciseCrossRef
import com.example.btl_android_project.local.entity.Exercise
import com.example.btl_android_project.repository.DailyDiaryRepository
import com.example.btl_android_project.repository.DiaryExerciseCrossRefRepository
import com.example.btl_android_project.repository.ExercisesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MyExercisesViewModel @Inject constructor(
    private val exercisesRepository: ExercisesRepository,
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val dailyDiaryRepository: DailyDiaryRepository,
    private val dairyExerciseCrossRefRepository: DiaryExerciseCrossRefRepository,
) : ViewModel() {

    private val userId = firebaseAuthDataSource.getCurrentUserId()
    val logDate: LocalDate = LocalDate.now()

    private val _exercises = MutableLiveData<List<Exercise>>()
    val exercises: LiveData<List<Exercise>> = _exercises


    fun loadExercisesForCurrentUser() {
        viewModelScope.launch {
            try {
                exercisesRepository.getAllExercisesByUser(userId.toString()).collect { exercises ->
                    Timber.d("Loaded exercises: $exercises")
                    _exercises.value = exercises
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load log exercises: ${e.message}")
                _exercises.value = emptyList()
            }
        }
    }


    fun updateExercise(exercise: Exercise) {
        viewModelScope.launch {
            exercisesRepository.updateExercise(exercise)
            loadExercisesForCurrentUser()
        }
    }

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            exercisesRepository.deleteExercise(exercise)
            loadExercisesForCurrentUser()
        }
    }

    fun addExerciseDiary(
        exerciseId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val dailyDiary = dailyDiaryRepository.getOrCreateDailyDiary(userId.toString(), logDate)
            val exerciseCrossRef = DiaryExerciseCrossRef(
                diaryId = dailyDiary.id,
                exerciseId = exerciseId,
                userId = userId.toString(),
                servings = 1
            )

            dairyExerciseCrossRefRepository.insertOrUpdateDiaryExerciseCrossRef(exerciseCrossRef)

            val exercise = exercisesRepository.getExerciseById(exerciseId)
            val updatedDailyDiary = dailyDiary.copy(
                totalExerciseCalories = dailyDiary.totalExerciseCalories + (exercise?.caloriesBurned ?: 0f) * exerciseCrossRef.servings
            )

            dailyDiaryRepository.updateDailyDiary(updatedDailyDiary)
            onSuccess()
        }
    }

}
