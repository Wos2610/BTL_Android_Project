package com.example.btl_android_project.presentation.log_weight

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.auth.FirebaseAuthDataSource
import com.example.btl_android_project.local.entity.LogWeight
import com.example.btl_android_project.local.entity.UserProfile
import com.example.btl_android_project.repository.LogWeightRepository
import com.example.btl_android_project.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LogWeightViewModel @Inject constructor(
    private val logWeightRepository: LogWeightRepository,
    private val userProfileRepository: UserProfileRepository,
    private val firebaseAuthDataSource: FirebaseAuthDataSource
) : ViewModel() {

    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> get() = _saveStatus

    private val _updateStatus = MutableLiveData<Boolean>()
    val updateStatus: LiveData<Boolean> get() = _updateStatus

    private val _deleteStatus = MutableLiveData<Boolean>()
    val deleteStatus: LiveData<Boolean> get() = _deleteStatus

    private val _logWeights = MutableLiveData<List<LogWeight>>()
    val logWeights: LiveData<List<LogWeight>> get() = _logWeights

    private val userId = firebaseAuthDataSource.getCurrentUserId()
    val logDate: LocalDate = LocalDate.now()

    fun loadLogWeights() {
        viewModelScope.launch {
            try {
                logWeightRepository.getAllLogWeightsByUser(userId.toString()).collect { logs ->
                    Timber.d("Loaded log weights: $logs")
                    _logWeights.value = logs
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load log weights: ${e.message}")
                _logWeights.value = emptyList()
            }
        }
    }

    fun saveLogWeight(weight: Float, date: String) {
        viewModelScope.launch {
            try {
                val logs = logWeightRepository.getAllLogWeightsByUser(userId.toString()).first()
                val existingLogForDate = logs.find { it.date == date }

                if (existingLogForDate != null) {
                    val updatedLog = existingLogForDate.copy(weight = weight, updatedAt = System.currentTimeMillis())
                    logWeightRepository.updateLogWeight(updatedLog)
                } else {
                    val logWeight = LogWeight(
                        userId = userId.toString(),
                        weight = weight,
                        date = date
                    )
                    logWeightRepository.insertLogWeight(logWeight)
                }

                val latestLog = (logs + listOfNotNull(existingLogForDate?.copy(weight = weight))
                    .ifEmpty { listOf(LogWeight(userId = userId.toString(), weight = weight, date = date)) })
                    .maxByOrNull { it.date }

                if (latestLog?.date == date) {
                    Timber.d("Current weight updated: $weight kg")
                    val userProfile = userProfileRepository.getUserProfileByUserId(userId.toString())
                    val updateUserProfile = userProfile?.copy(
                        currentWeight = weight
                    )
                    updateUserProfile?.let { userProfileRepository.upsertUserProfile(it) }
                }


                _saveStatus.value = true
            } catch (e: Exception) {
                Timber.e("Failed to save log weight: ${e.message}")
                _saveStatus.value = false
            }
        }
    }




    fun deleteLogWeight(logWeightId: String) {
        viewModelScope.launch {
            try {
                val logWeight = LogWeight(
                    id = logWeightId
                )
                logWeightRepository.deleteLogWeight(logWeight)
                _deleteStatus.value = true
            } catch (e: Exception) {
                Timber.e("Failed to delete log weight: ${e.message}")
                _deleteStatus.value = false
            }
        }
    }
}