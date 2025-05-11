package com.example.btl_android_project.presentation.log_weight

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.auth.FirebaseAuthDataSource
import com.example.btl_android_project.local.entity.LogWeight
import com.example.btl_android_project.repository.LogWeightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LogWeightViewModel @Inject constructor(
    private val logWeightRepository: LogWeightRepository,
    private val firebaseAuthDataSource: FirebaseAuthDataSource
) : ViewModel() {

    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> get() = _saveStatus

    private val userId = firebaseAuthDataSource.getCurrentUserId()
    val logDate: LocalDate = LocalDate.now()

    fun saveLogWeight(weight: Float, date: String) {
        viewModelScope.launch {
            try {
                val logWeight = LogWeight(
                    userId = userId.toString(),
                    weight = weight,
                    date = date
                )
                logWeightRepository.insertLogWeight(logWeight)
                _saveStatus.value = true
            } catch (e: Exception) {
                Timber.e("Failed to save log weight: ${e.message}")
                _saveStatus.value = false
            }
        }
    }
}