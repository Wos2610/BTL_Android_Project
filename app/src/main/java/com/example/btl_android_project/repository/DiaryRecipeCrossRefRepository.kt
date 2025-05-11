package com.example.btl_android_project.repository

import android.util.Log
import com.example.btl_android_project.firestore.datasource.DiaryRecipeCrossRefFireStoreDataSourceImpl
import com.example.btl_android_project.local.dao.DiaryRecipeCrossRefDao
import com.example.btl_android_project.local.entity.DiaryRecipeCrossRef
import com.example.btl_android_project.local.enums.MealType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DiaryRecipeCrossRefRepository @Inject constructor(
    private val diaryRecipeCrossRefDao: DiaryRecipeCrossRefDao,
    private val diaryRecipeCrossRefFireStoreDataSource: DiaryRecipeCrossRefFireStoreDataSourceImpl
) {
    private val TAG = "DiaryRecipeCrossRefRepo"

    suspend fun getDiaryRecipeCrossRefsByDiaryId(diaryId: String): List<DiaryRecipeCrossRef>? {
        return withContext(Dispatchers.IO) {
            diaryRecipeCrossRefDao.getDiaryRecipeCrossRefsByDiaryId(diaryId)
        }
    }

    suspend fun insertDiaryRecipeCrossRef(crossRef: DiaryRecipeCrossRef): Long {
        return withContext(Dispatchers.IO) {
            val id = diaryRecipeCrossRefDao.insertDiaryRecipeCrossRef(crossRef)
            Log.d(TAG, "Inserted DiaryRecipeCrossRef: diaryId=${crossRef.diaryId}, recipeId=${crossRef.recipeId}")

            diaryRecipeCrossRefFireStoreDataSource.insertDiaryRecipeCrossRef(crossRef)
            
            id
        }
    }

    suspend fun pullFromFireStore(diaryId: String) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Pulling diary-recipe cross references from Firestore for diaryId=$diaryId")
            val crossRefs = diaryRecipeCrossRefFireStoreDataSource.getDiaryRecipeCrossRefsByDiaryId(diaryId)
            Log.d(TAG, "Pulled ${crossRefs.size} diary-recipe cross references from Firestore")
            if (crossRefs.isNotEmpty()) {
                diaryRecipeCrossRefDao.deleteAndInsertInTransaction(diaryId, crossRefs)
            }
        }
    }

    suspend fun deleteDiaryRecipeCrossRef(userId: String, diaryId: String, recipeId: String, mealType: MealType) {
        withContext(Dispatchers.IO) {
            diaryRecipeCrossRefDao.deleteByUserIdDiaryIdRecipeIdMealType(userId, diaryId, recipeId, mealType.name)
            diaryRecipeCrossRefFireStoreDataSource.deleteByUserIdDiaryIdRecipeIdMealType(userId, diaryId, recipeId, mealType.name)
        }
    }
}