package com.example.btl_android_project.repository

import android.util.Log
import com.example.btl_android_project.firestore.datasource.DiaryRecipeCrossRefFireStoreDataSourceImpl
import com.example.btl_android_project.local.dao.DiaryRecipeCrossRefDao
import com.example.btl_android_project.local.entity.DiaryRecipeCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DiaryRecipeCrossRefRepository @Inject constructor(
    private val diaryRecipeCrossRefDao: DiaryRecipeCrossRefDao,
    private val diaryRecipeCrossRefFireStoreDataSource: DiaryRecipeCrossRefFireStoreDataSourceImpl
) {
    private val TAG = "DiaryRecipeCrossRefRepo"
    
    /**
     * Get all diary-recipe cross references
     */
    suspend fun getDiaryRecipeCrossRefs(): List<DiaryRecipeCrossRef> {
        return withContext(Dispatchers.IO) {
            diaryRecipeCrossRefDao.getDiaryRecipeCrossRefs()
        }
    }
    
    /**
     * Get diary-recipe cross references by diary ID
     */
    suspend fun getDiaryRecipeCrossRefsByDiaryId(diaryId: Int): List<DiaryRecipeCrossRef>? {
        return withContext(Dispatchers.IO) {
            diaryRecipeCrossRefDao.getDiaryRecipeCrossRefsByDiaryId(diaryId)
        }
    }
    
    /**
     * Insert a new diary-recipe cross reference
     */
    suspend fun insertDiaryRecipeCrossRef(crossRef: DiaryRecipeCrossRef): Long {
        return withContext(Dispatchers.IO) {
            val id = diaryRecipeCrossRefDao.insertDiaryRecipeCrossRef(crossRef)
            Log.d(TAG, "Inserted DiaryRecipeCrossRef: diaryId=${crossRef.diaryId}, recipeId=${crossRef.recipeId}")
            
            // Add to Firestore
            diaryRecipeCrossRefFireStoreDataSource.insertDiaryRecipeCrossRef(crossRef)
            
            id
        }
    }
    
    /**
     * Update an existing diary-recipe cross reference
     */
    suspend fun updateDiaryRecipeCrossRef(crossRef: DiaryRecipeCrossRef) {
        withContext(Dispatchers.IO) {
            diaryRecipeCrossRefDao.updateDiaryRecipeCrossRef(crossRef)
            Log.d(TAG, "Updated DiaryRecipeCrossRef: diaryId=${crossRef.diaryId}, recipeId=${crossRef.recipeId}")
            
            // Update in Firestore
            diaryRecipeCrossRefFireStoreDataSource.updateDiaryRecipeCrossRef(crossRef)
        }
    }
    
    /**
     * Delete a diary-recipe cross reference
     */
    suspend fun deleteDiaryRecipeCrossRef(crossRef: DiaryRecipeCrossRef) {
        withContext(Dispatchers.IO) {
            diaryRecipeCrossRefDao.deleteDiaryRecipeCrossRef(crossRef)
            Log.d(TAG, "Deleted DiaryRecipeCrossRef: diaryId=${crossRef.diaryId}, recipeId=${crossRef.recipeId}")
            
            // Delete from Firestore
            diaryRecipeCrossRefFireStoreDataSource.deleteDiaryRecipeCrossRef(crossRef)
        }
    }
    
    /**
     * Delete all diary-recipe cross references for a specific diary
     */
    suspend fun deleteDiaryRecipeCrossRefsByDiaryId(diaryId: Int) {
        withContext(Dispatchers.IO) {
            diaryRecipeCrossRefDao.deleteDiaryRecipeCrossRefsByDiaryId(diaryId)
            Log.d(TAG, "Deleted all DiaryRecipeCrossRefs for diaryId=$diaryId")
            
            // Delete from Firestore
            diaryRecipeCrossRefFireStoreDataSource.deleteDiaryRecipeCrossRefsByDiaryId(diaryId)
        }
    }
    
    /**
     * Pull diary-recipe cross references from Firestore
     */
    suspend fun pullFromFireStore(diaryId: Int) {
        Log.d(TAG, "Pulling diary-recipe cross references from Firestore for diaryId=$diaryId")
        val crossRefs = diaryRecipeCrossRefFireStoreDataSource.getDiaryRecipeCrossRefsByDiaryId(diaryId)
        Log.d(TAG, "Pulled ${crossRefs.size} diary-recipe cross references from Firestore")
        
        withContext(Dispatchers.IO) {
            crossRefs.forEach { crossRef ->
                // Check if it already exists
                val existing = diaryRecipeCrossRefDao.getDiaryRecipeCrossRef(crossRef.diaryId, crossRef.recipeId)
                if (existing == null) {
                    diaryRecipeCrossRefDao.insertDiaryRecipeCrossRef(crossRef)
                } else {
                    diaryRecipeCrossRefDao.updateDiaryRecipeCrossRef(crossRef)
                }
            }
        }
    }
}