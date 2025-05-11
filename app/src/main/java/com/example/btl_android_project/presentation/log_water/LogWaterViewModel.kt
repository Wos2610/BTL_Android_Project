package com.example.btl_android_project.presentation.log_water


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.auth.FirebaseAuthDataSource
import com.example.btl_android_project.local.entity.LogWater
import com.example.btl_android_project.repository.DailyDiaryRepository
import com.example.btl_android_project.repository.LogWaterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LogWaterViewModel @Inject constructor(
    private val logWaterRepository: LogWaterRepository,
    private val dailyDiaryRepository: DailyDiaryRepository,
    private val firebaseAuthDataSource: FirebaseAuthDataSource
) : ViewModel() {

    private val _logWater = MutableStateFlow<LogWater?>(null)
    val logWater: StateFlow<LogWater?> get() = _logWater

    private val _amountMl = MutableStateFlow(0)
    val amountMl: StateFlow<Int> = _amountMl


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved

    private val userId = firebaseAuthDataSource.getCurrentUserId()
    val logDate: LocalDate = LocalDate.now()

    fun setAmountMl(amount: Int) {
        _amountMl.value = amount
    }


    private val _totalWater = MutableLiveData(0)
    val totalWater: LiveData<Int> get() = _totalWater

    fun addWater(amount: Int) {
        _totalWater.value = (_totalWater.value ?: 0) + amount
    }

    fun setWater(amount: Int) {
        _totalWater.value = amount
    }

    fun validateLogWaterInfo(): Boolean {
        val amount = _totalWater.value ?: 0
        if (amount < 0) return false
        return true
    }

    fun saveLogWater(onSuccess: () -> Unit) {
        if (!validateLogWaterInfo()) return
        viewModelScope.launch {
            val dailyDiary = dailyDiaryRepository.getOrCreateDailyDiary(userId.toString(), logDate)
            _isLoading.value = true
            val amountMlToSave = _totalWater.value ?: 0

            val log = LogWater(
                userId = userId.toString(),
                dailyDiaryId = dailyDiary.id,
                amountMl = amountMlToSave,
            )

            val updatedDailyDiary = dailyDiary.copy(
                totalWaterMl = dailyDiary.totalWaterMl + (amountMlToSave)
            )
            dailyDiaryRepository.updateDailyDiary(updatedDailyDiary)
            logWaterRepository.addLogWater(log)
            _totalWater.value = 0
            _isSaved.value = true
            _isLoading.value = false
        }
        onSuccess()
    }

    fun loadLog(logId: String) {
        viewModelScope.launch {
            _logWater.value = logWaterRepository.getLogWaterById(logId)
        }
    }
}
