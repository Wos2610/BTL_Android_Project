package com.example.btl_android_project.repository

import android.util.Log
import com.example.btl_android_project.firestore.datasource.DiaryMealCrossRefFireStoreDataSourceImpl
import com.example.btl_android_project.local.dao.DiaryMealCrossRefDao
import com.example.btl_android_project.local.entity.DiaryMealCrossRef
import com.example.btl_android_project.local.enums.MealType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DiaryMealCrossRefRepository @Inject constructor(
    private val diaryMealCrossRefDao: DiaryMealCrossRefDao,
    private val diaryMealCrossRefFireStoreDataSource: DiaryMealCrossRefFireStoreDataSourceImpl
) {
    private val TAG = "DiaryMealCrossRefRepo"

    suspend fun getDiaryMealCrossRefsByDiaryId(diaryId: String): List<DiaryMealCrossRef>? {
        return withContext(Dispatchers.IO) {
            diaryMealCrossRefDao.getDiaryMealCrossRefsByDiaryId(diaryId)
        }
    }

    suspend fun insertDiaryMealCrossRef(crossRef: DiaryMealCrossRef): Long {
        return withContext(Dispatchers.IO) {
            val id = diaryMealCrossRefDao.insertDiaryMealCrossRef(crossRef)
            Log.d(TAG, "Inserted DiaryMealCrossRef: diaryId=${crossRef.diaryId}, mealId=${crossRef.mealId}")

            diaryMealCrossRefFireStoreDataSource.insertDiaryMealCrossRef(crossRef)
            
            id
        }
    }

    suspend fun pullFromFireStore(diaryId: String) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Pulling diary-meal cross references from Firestore for diaryId=$diaryId")
            val crossRefs = diaryMealCrossRefFireStoreDataSource.getDiaryMealCrossRefsByDiaryId(diaryId)
            Log.d(TAG, "Pulled ${crossRefs.size} diary-meal cross references from Firestore")
            if (crossRefs.isNotEmpty()) {
                diaryMealCrossRefDao.deleteAndInsertInTransaction(diaryId, crossRefs)
            }
        }
    }

    suspend fun deleteDiaryMealCrossRef(userId: String, diaryId: String, mealId: String, mealType: MealType) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Deleted DiaryMealCrossRef: userId=$userId, diaryId=$diaryId, mealId=$mealId, mealType=$mealType")
            diaryMealCrossRefDao.deleteByUserIdDiaryIdMealIdMealType(userId, diaryId, mealId, mealType.name)
            diaryMealCrossRefFireStoreDataSource.deleteByUserIdDiaryIdMealIdMealType(userId, diaryId, mealId, mealType.name)
        }
    }

    suspend fun updateDiaryMealCrossRef(
        userId: String,
        diaryId: String,
        mealId: String,
        mealType: MealType,
        servings: Int
    ) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Updated DiaryMealCrossRef: userId=$userId, diaryId=$diaryId, mealId=$mealId, mealType=$mealType")
            diaryMealCrossRefDao.updateByUserIdDiaryIdMealIdMealType(userId, diaryId, mealId, mealType.name, servings)
            diaryMealCrossRefFireStoreDataSource.updateByUserIdDiaryIdMealIdMealType(userId, diaryId, mealId, mealType.name, servings)
        }
    }

    suspend fun pullFromFireStoreByUserId(userId: String) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Pulling diary-meal cross references from Firestore for userId=$userId")
            val crossRefs = diaryMealCrossRefFireStoreDataSource.getDiaryMealCrossRefsByUserId(userId)
            Log.d(TAG, "Pulled ${crossRefs.size} diary-meal cross references from Firestore")
            if (crossRefs.isNotEmpty()) {
                diaryMealCrossRefDao.deleteAllDiaryMealCrossRefs()
                diaryMealCrossRefDao.insertAll(crossRefs)
            }
        }
    }

    suspend fun pullFromFireStoreByDiaryIds(diaryIds: List<String>) {
        withContext(Dispatchers.IO) {
            val allCrossRefs = diaryMealCrossRefFireStoreDataSource.getAllByDiaryIds(diaryIds)
            diaryMealCrossRefDao.deleteAllForDiaries(diaryIds)
            diaryMealCrossRefDao.insertAll(allCrossRefs)
        }
    }
}