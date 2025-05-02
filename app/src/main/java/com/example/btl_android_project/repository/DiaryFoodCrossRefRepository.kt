package com.example.btl_android_project.repository

import android.util.Log
import com.example.btl_android_project.firestore.datasource.DiaryFoodCrossRefFireStoreDataSourceImpl
import com.example.btl_android_project.local.dao.DiaryFoodCrossRefDao
import com.example.btl_android_project.local.entity.DiaryFoodCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DiaryFoodCrossRefRepository @Inject constructor(
    private val diaryFoodCrossRefDao: DiaryFoodCrossRefDao,
    private val diaryFoodCrossRefFireStoreDataSource: DiaryFoodCrossRefFireStoreDataSourceImpl
) {
    private val TAG = "DiaryFoodCrossRefRepo"
    
    /**
     * Get all diary-food cross references
     */
    suspend fun getDiaryFoodCrossRefs(): List<DiaryFoodCrossRef> {
        return withContext(Dispatchers.IO) {
            diaryFoodCrossRefDao.getDiaryFoodCrossRefs()
        }
    }
    
    /**
     * Get diary-food cross references by diary ID
     */
    suspend fun getDiaryFoodCrossRefsByDiaryId(diaryId: Int): List<DiaryFoodCrossRef>? {
        return withContext(Dispatchers.IO) {
            diaryFoodCrossRefDao.getDiaryFoodCrossRefsByDiaryId(diaryId)
        }
    }
    
    /**
     * Insert a new diary-food cross reference
     */
    suspend fun insertDiaryFoodCrossRef(crossRef: DiaryFoodCrossRef): Long {
        return withContext(Dispatchers.IO) {
            val id = diaryFoodCrossRefDao.insertDiaryFoodCrossRef(crossRef)
            Log.d(TAG, "Inserted DiaryFoodCrossRef: diaryId=${crossRef.diaryId}, foodId=${crossRef.foodId}")
            
            // Add to Firestore
            diaryFoodCrossRefFireStoreDataSource.insertDiaryFoodCrossRef(crossRef)
            
            id
        }
    }
    
    /**
     * Update an existing diary-food cross reference
     */
    suspend fun updateDiaryFoodCrossRef(crossRef: DiaryFoodCrossRef) {
        withContext(Dispatchers.IO) {
            diaryFoodCrossRefDao.updateDiaryFoodCrossRef(crossRef)
            Log.d(TAG, "Updated DiaryFoodCrossRef: diaryId=${crossRef.diaryId}, foodId=${crossRef.foodId}")
            
            // Update in Firestore
            diaryFoodCrossRefFireStoreDataSource.updateDiaryFoodCrossRef(crossRef)
        }
    }
    
    /**
     * Delete a diary-food cross reference
     */
    suspend fun deleteDiaryFoodCrossRef(crossRef: DiaryFoodCrossRef) {
        withContext(Dispatchers.IO) {
            diaryFoodCrossRefDao.deleteDiaryFoodCrossRef(crossRef)
            Log.d(TAG, "Deleted DiaryFoodCrossRef: diaryId=${crossRef.diaryId}, foodId=${crossRef.foodId}")
            
            // Delete from Firestore
            diaryFoodCrossRefFireStoreDataSource.deleteDiaryFoodCrossRef(crossRef)
        }
    }
    
    /**
     * Delete all diary-food cross references for a specific diary
     */
    suspend fun deleteDiaryFoodCrossRefsByDiaryId(diaryId: Int) {
        withContext(Dispatchers.IO) {
            diaryFoodCrossRefDao.deleteDiaryFoodCrossRefsByDiaryId(diaryId)
            Log.d(TAG, "Deleted all DiaryFoodCrossRefs for diaryId=$diaryId")
            
            // Delete from Firestore
            diaryFoodCrossRefFireStoreDataSource.deleteDiaryFoodCrossRefsByDiaryId(diaryId)
        }
    }
    
    /**
     * Pull diary-food cross references from Firestore
     */
    suspend fun pullFromFireStore(diaryId: String) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Pulling diary-food cross references from Firestore for diaryId=$diaryId")
            val crossRefs = diaryFoodCrossRefFireStoreDataSource.getDiaryFoodCrossRefsByDiaryId(diaryId)
            Log.d(TAG, "Pulled ${crossRefs.size} diary-food cross references from Firestore")
            diaryFoodCrossRefDao.deleteAllDiaryFoodCrossRefs()
            diaryFoodCrossRefDao.insertAllDiaryFoodCrossRefs(crossRefs)
        }
    }
}