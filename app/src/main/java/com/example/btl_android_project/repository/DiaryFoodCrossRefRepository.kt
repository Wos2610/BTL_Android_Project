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
    private val diaryFoodCrossRefFireStoreDataSource: DiaryFoodCrossRefFireStoreDataSourceImpl,
) {
    private val TAG = "DiaryFoodCrossRefRepo"

    suspend fun getDiaryFoodCrossRefsByDiaryId(diaryId: String): List<DiaryFoodCrossRef>? {
        return withContext(Dispatchers.IO) {
            diaryFoodCrossRefDao.getDiaryFoodCrossRefsByDiaryId(diaryId)
        }
    }

    suspend fun insertDiaryFoodCrossRef(crossRef: DiaryFoodCrossRef): Long {
        return withContext(Dispatchers.IO) {
            val id = diaryFoodCrossRefDao.insertDiaryFoodCrossRef(crossRef)
            Log.d(
                TAG,
                "Inserted DiaryFoodCrossRef: diaryId=${crossRef.diaryId}, foodId=${crossRef.foodId}, mealType=${crossRef.mealType}"
            )

            diaryFoodCrossRefFireStoreDataSource.insertDiaryFoodCrossRef(crossRef)

            id
        }
    }

    suspend fun insertOrUpdateDiaryFoodCrossRef(crossRef: DiaryFoodCrossRef) {
        return withContext(Dispatchers.IO) {
            val existingCrossRef = diaryFoodCrossRefDao.getDiaryFoodCrossRefByMealType(
                crossRef.diaryId,
                crossRef.foodId,
                crossRef.mealType.name
            )
            Log.d(TAG, "Existing DiaryFoodCrossRef: $existingCrossRef")
            if (existingCrossRef != null) {
                existingCrossRef.servings += crossRef.servings
                updateDiaryFoodCrossRef(existingCrossRef)
                Log.d(
                    TAG,
                    "Updated existing DiaryFoodCrossRef: diaryId=${crossRef.diaryId}, foodId=${crossRef.foodId}"
                )
            } else {
                insertDiaryFoodCrossRef(crossRef)
            }
        }
    }

    suspend fun updateDiaryFoodCrossRef(crossRef: DiaryFoodCrossRef) {
        withContext(Dispatchers.IO) {
            diaryFoodCrossRefDao.updateDiaryFoodCrossRef(crossRef)
            Log.d(
                TAG,
                "Updated DiaryFoodCrossRef: diaryId=${crossRef.diaryId}, foodId=${crossRef.foodId}"
            )

            diaryFoodCrossRefFireStoreDataSource.updateDiaryFoodCrossRef(crossRef)
        }
    }

    suspend fun pullFromFireStore(diaryId: String) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Pulling diary-food cross references from Firestore for diaryId=$diaryId")

            val crossRefs = diaryFoodCrossRefFireStoreDataSource.getDiaryFoodCrossRefsByDiaryId(diaryId)
            Log.d(TAG, "Pulled ${crossRefs.size} diary-food cross references from Firestore")

            if (crossRefs.isNotEmpty()) {
                diaryFoodCrossRefDao.deleteAndInsertInTransaction(diaryId, crossRefs)
            }
        }
    }
}