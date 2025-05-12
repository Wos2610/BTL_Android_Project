package com.example.btl_android_project.presentation.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.auth.FirebaseAuthDataSource
import com.example.btl_android_project.local.entity.DailyDiary
import com.example.btl_android_project.local.entity.LogWeight
import com.example.btl_android_project.repository.DailyDiaryRepository
import com.example.btl_android_project.repository.LogWeightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val dailyDiaryRepository: DailyDiaryRepository,
    private val logWeightRepository: LogWeightRepository,
) : ViewModel() {
    val currentUserId = firebaseAuthDataSource.getCurrentUserId().toString()
    val lodDate = LocalDate.now()
    val _diary: MutableStateFlow<DailyDiary?> = MutableStateFlow(null)
    val todayDiary = _diary.asStateFlow()

    fun getTodayDiary(){
        viewModelScope.launch {
            val diary = dailyDiaryRepository.getDailyDiaryByDate(
                userId = currentUserId,
                date = lodDate,
            )
            _diary.value = diary
        }
    }

    private val _latestWeights = MutableLiveData<List<LogWeight>>()
    val latestWeights: LiveData<List<LogWeight>> get() = _latestWeights

    fun getLatestWeights() {
        viewModelScope.launch {
            val logs = logWeightRepository.getLast5Weights(currentUserId)
            _latestWeights.value = logs.sortedBy { it.date }
        }
    }

}