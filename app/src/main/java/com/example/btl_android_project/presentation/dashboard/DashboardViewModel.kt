package com.example.btl_android_project.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.auth.FirebaseAuthDataSource
import com.example.btl_android_project.local.entity.DailyDiary
import com.example.btl_android_project.repository.DailyDiaryRepository
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


}