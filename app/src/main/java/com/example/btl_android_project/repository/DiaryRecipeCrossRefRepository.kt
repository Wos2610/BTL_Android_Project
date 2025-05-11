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

    suspend fun getDiaryRecipeCrossRefs(): List<DiaryRecipeCrossRef> {
        return withContext(Dispatchers.IO) {
            diaryRecipeCrossRefDao.getDiaryRecipeCrossRefs()
        }
    }

    suspend fun getDiaryRecipeCrossRefsByDiaryId(diaryId: String): List<DiaryRecipeCrossRef>? {
        return withContext(Dispatchers.IO) {
            diaryRecipeCrossRefDao.getDiaryRecipeCrossRefsByDiaryId(diaryId)
        }
    }

    suspend fun insertDiaryRecipeCrossRef(crossRef: DiaryRecipeCrossRef): Long {
        return withContext(Dispatchers.IO) {
            val id = diaryRecipeCrossRefDao.insertDiaryRecipeCrossRef(crossRef)
            Log.d(TAG, "Inserted DiaryRecipeCrossRef: diaryId=${crossRef.diaryId}, recipeId=${crossRef.recipeId}")
            
            // Add to Firestore
            diaryRecipeCrossRefFireStoreDataSource.insertDiaryRecipeCrossRef(crossRef)
            
            id
        }
    }

    suspend fun updateDiaryRecipeCrossRef(crossRef: DiaryRecipeCrossRef) {
        withContext(Dispatchers.IO) {
            diaryRecipeCrossRefDao.updateDiaryRecipeCrossRef(crossRef)
            Log.d(TAG, "Updated DiaryRecipeCrossRef: diaryId=${crossRef.diaryId}, recipeId=${crossRef.recipeId}")
            
            // Update in Firestore
            diaryRecipeCrossRefFireStoreDataSource.updateDiaryRecipeCrossRef(crossRef)
        }
    }

    suspend fun deleteDiaryRecipeCrossRef(crossRef: DiaryRecipeCrossRef) {
        withContext(Dispatchers.IO) {
            diaryRecipeCrossRefDao.deleteDiaryRecipeCrossRef(crossRef)
            Log.d(TAG, "Deleted DiaryRecipeCrossRef: diaryId=${crossRef.diaryId}, recipeId=${crossRef.recipeId}")
            
            // Delete from Firestore
            diaryRecipeCrossRefFireStoreDataSource.deleteDiaryRecipeCrossRef(crossRef)
        }
    }

    suspend fun deleteDiaryRecipeCrossRefsByDiaryId(diaryId: String) {
        withContext(Dispatchers.IO) {
            diaryRecipeCrossRefDao.deleteDiaryRecipeCrossRefsByDiaryId(diaryId)
            Log.d(TAG, "Deleted all DiaryRecipeCrossRefs for diaryId=$diaryId")
            
            // Delete from Firestore
            diaryRecipeCrossRefFireStoreDataSource.deleteDiaryRecipeCrossRefsByDiaryId(diaryId)
        }
    }

    suspend fun pullFromFireStore(diaryId: String) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Pulling diary-recipe cross references from Firestore for diaryId=$diaryId")
            val crossRefs = diaryRecipeCrossRefFireStoreDataSource.getDiaryRecipeCrossRefsByDiaryId(diaryId)
            Log.d(TAG, "Pulled ${crossRefs.size} diary-recipe cross references from Firestore")
            diaryRecipeCrossRefDao.insertAllDiaryRecipeCrossRefs(crossRefs)
        }
    }

    suspend fun getDiaryRecipeCrossRef(diaryId: String, recipeId: String): DiaryRecipeCrossRef? {
        return withContext(Dispatchers.IO) {
            diaryRecipeCrossRefDao.getDiaryRecipeCrossRef(diaryId, recipeId)
        }
    }
}