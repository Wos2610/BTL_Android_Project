package com.example.btl_android_project.repository

import android.util.Log
import com.example.btl_android_project.firestore.datasource.DiaryMealCrossRefFireStoreDataSourceImpl
import com.example.btl_android_project.local.dao.DiaryMealCrossRefDao
import com.example.btl_android_project.local.entity.DiaryMealCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DiaryMealCrossRefRepository @Inject constructor(
    private val diaryMealCrossRefDao: DiaryMealCrossRefDao,
    private val diaryMealCrossRefFireStoreDataSource: DiaryMealCrossRefFireStoreDataSourceImpl
) {
    private val TAG = "DiaryMealCrossRefRepo"

    suspend fun getDiaryMealCrossRefs(): List<DiaryMealCrossRef> {
        return withContext(Dispatchers.IO) {
            diaryMealCrossRefDao.getDiaryMealCrossRefs()
        }
    }

    suspend fun getDiaryMealCrossRefsByDiaryId(diaryId: String): List<DiaryMealCrossRef>? {
        return withContext(Dispatchers.IO) {
            diaryMealCrossRefDao.getDiaryMealCrossRefsByDiaryId(diaryId)
        }
    }

    suspend fun insertDiaryMealCrossRef(crossRef: DiaryMealCrossRef): Long {
        return withContext(Dispatchers.IO) {
            val id = diaryMealCrossRefDao.insertDiaryMealCrossRef(crossRef)
            Log.d(TAG, "Inserted DiaryMealCrossRef: diaryId=${crossRef.diaryId}, mealId=${crossRef.mealId}")
            
            // Add to Firestore
            diaryMealCrossRefFireStoreDataSource.insertDiaryMealCrossRef(crossRef)
            
            id
        }
    }

    suspend fun updateDiaryMealCrossRef(crossRef: DiaryMealCrossRef) {
        withContext(Dispatchers.IO) {
            diaryMealCrossRefDao.updateDiaryMealCrossRef(crossRef)
            Log.d(TAG, "Updated DiaryMealCrossRef: diaryId=${crossRef.diaryId}, mealId=${crossRef.mealId}")
            
            // Update in Firestore
            diaryMealCrossRefFireStoreDataSource.updateDiaryMealCrossRef(crossRef)
        }
    }

    suspend fun deleteDiaryMealCrossRef(crossRef: DiaryMealCrossRef) {
        withContext(Dispatchers.IO) {
            diaryMealCrossRefDao.deleteDiaryMealCrossRef(crossRef)
            Log.d(TAG, "Deleted DiaryMealCrossRef: diaryId=${crossRef.diaryId}, mealId=${crossRef.mealId}")
            
            // Delete from Firestore
            diaryMealCrossRefFireStoreDataSource.deleteDiaryMealCrossRef(crossRef)
        }
    }

    suspend fun deleteDiaryMealCrossRefsByDiaryId(diaryId: String) {
        withContext(Dispatchers.IO) {
            diaryMealCrossRefDao.deleteDiaryMealCrossRefsByDiaryId(diaryId)
            Log.d(TAG, "Deleted all DiaryMealCrossRefs for diaryId=$diaryId")
            
            // Delete from Firestore
            diaryMealCrossRefFireStoreDataSource.deleteDiaryMealCrossRefsByDiaryId(diaryId)
        }
    }

    suspend fun pullFromFireStore(diaryId: String) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Pulling diary-meal cross references from Firestore for diaryId=$diaryId")
            val crossRefs = diaryMealCrossRefFireStoreDataSource.getDiaryMealCrossRefsByDiaryId(diaryId)
            Log.d(TAG, "Pulled ${crossRefs.size} diary-meal cross references from Firestore")
            diaryMealCrossRefDao.insertAllDiaryMealCrossRefs(crossRefs)
        }
    }

    suspend fun getDiaryMealCrossRef(diaryId: String, mealId: String): DiaryMealCrossRef? {
        return withContext(Dispatchers.IO) {
            diaryMealCrossRefDao.getDiaryMealCrossRef(diaryId, mealId)
        }
    }
}