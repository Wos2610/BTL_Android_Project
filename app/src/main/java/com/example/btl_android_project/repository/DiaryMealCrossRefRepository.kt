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
    
    /**
     * Get all diary-meal cross references
     */
    suspend fun getDiaryMealCrossRefs(): List<DiaryMealCrossRef> {
        return withContext(Dispatchers.IO) {
            diaryMealCrossRefDao.getDiaryMealCrossRefs()
        }
    }
    
    /**
     * Get diary-meal cross references by diary ID
     */
    suspend fun getDiaryMealCrossRefsByDiaryId(diaryId: Int): List<DiaryMealCrossRef>? {
        return withContext(Dispatchers.IO) {
            diaryMealCrossRefDao.getDiaryMealCrossRefsByDiaryId(diaryId)
        }
    }
    
    /**
     * Insert a new diary-meal cross reference
     */
    suspend fun insertDiaryMealCrossRef(crossRef: DiaryMealCrossRef): Long {
        return withContext(Dispatchers.IO) {
            val id = diaryMealCrossRefDao.insertDiaryMealCrossRef(crossRef)
            Log.d(TAG, "Inserted DiaryMealCrossRef: diaryId=${crossRef.diaryId}, mealId=${crossRef.mealId}")
            
            // Add to Firestore
            diaryMealCrossRefFireStoreDataSource.insertDiaryMealCrossRef(crossRef)
            
            id
        }
    }
    
    /**
     * Update an existing diary-meal cross reference
     */
    suspend fun updateDiaryMealCrossRef(crossRef: DiaryMealCrossRef) {
        withContext(Dispatchers.IO) {
            diaryMealCrossRefDao.updateDiaryMealCrossRef(crossRef)
            Log.d(TAG, "Updated DiaryMealCrossRef: diaryId=${crossRef.diaryId}, mealId=${crossRef.mealId}")
            
            // Update in Firestore
            diaryMealCrossRefFireStoreDataSource.updateDiaryMealCrossRef(crossRef)
        }
    }
    
    /**
     * Delete a diary-meal cross reference
     */
    suspend fun deleteDiaryMealCrossRef(crossRef: DiaryMealCrossRef) {
        withContext(Dispatchers.IO) {
            diaryMealCrossRefDao.deleteDiaryMealCrossRef(crossRef)
            Log.d(TAG, "Deleted DiaryMealCrossRef: diaryId=${crossRef.diaryId}, mealId=${crossRef.mealId}")
            
            // Delete from Firestore
            diaryMealCrossRefFireStoreDataSource.deleteDiaryMealCrossRef(crossRef)
        }
    }
    
    /**
     * Delete all diary-meal cross references for a specific diary
     */
    suspend fun deleteDiaryMealCrossRefsByDiaryId(diaryId: Int) {
        withContext(Dispatchers.IO) {
            diaryMealCrossRefDao.deleteDiaryMealCrossRefsByDiaryId(diaryId)
            Log.d(TAG, "Deleted all DiaryMealCrossRefs for diaryId=$diaryId")
            
            // Delete from Firestore
            diaryMealCrossRefFireStoreDataSource.deleteDiaryMealCrossRefsByDiaryId(diaryId)
        }
    }
    
    /**
     * Pull diary-meal cross references from Firestore
     */
    suspend fun pullFromFireStore(diaryId: Int) {
        Log.d(TAG, "Pulling diary-meal cross references from Firestore for diaryId=$diaryId")
        val crossRefs = diaryMealCrossRefFireStoreDataSource.getDiaryMealCrossRefsByDiaryId(diaryId)
        Log.d(TAG, "Pulled ${crossRefs.size} diary-meal cross references from Firestore")
        
        withContext(Dispatchers.IO) {
            crossRefs.forEach { crossRef ->
                // Check if it already exists
                val existing = diaryMealCrossRefDao.getDiaryMealCrossRef(crossRef.diaryId, crossRef.mealId)
                if (existing == null) {
                    diaryMealCrossRefDao.insertDiaryMealCrossRef(crossRef)
                } else {
                    diaryMealCrossRefDao.updateDiaryMealCrossRef(crossRef)
                }
            }
        }
    }
}