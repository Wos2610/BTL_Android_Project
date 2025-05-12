package com.example.btl_android_project.repository

import android.util.Log
import com.example.btl_android_project.firestore.datasource.DiaryExerciseCrossRefFireStoreDataSourceImpl
import com.example.btl_android_project.local.dao.DiaryExerciseCrossRefDao
import com.example.btl_android_project.local.entity.DiaryExerciseCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DiaryExerciseCrossRefRepository @Inject constructor(
    private val diaryExerciseCrossRefDao: DiaryExerciseCrossRefDao,
    private val diaryExerciseCrossRefFireStoreDataSource: DiaryExerciseCrossRefFireStoreDataSourceImpl
) {
    private val TAG = "DiaryExerciseCrossRefRepo"

    suspend fun getDiaryExerciseCrossRefsByDiaryId(diaryId: String): List<DiaryExerciseCrossRef>? {
        return withContext(Dispatchers.IO) {
            diaryExerciseCrossRefDao.getDiaryExerciseCrossRefsByDiaryId(diaryId)
        }
    }

    suspend fun insertDiaryExerciseCrossRef(crossRef: DiaryExerciseCrossRef): Long {
        return withContext(Dispatchers.IO) {
            val id = diaryExerciseCrossRefDao.insertDiaryExerciseCrossRef(crossRef)
            Log.d(
                TAG,
                "Inserted DiaryExerciseCrossRef: diaryId=${crossRef.diaryId}, exerciseId=${crossRef.exerciseId}"
            )

            diaryExerciseCrossRefFireStoreDataSource.insertDiaryExerciseCrossRef(crossRef)

            id
        }
    }

    suspend fun updateDiaryExerciseCrossRef(crossRef: DiaryExerciseCrossRef) {
        withContext(Dispatchers.IO) {
            diaryExerciseCrossRefDao.updateDiaryExerciseCrossRef(crossRef)
            Log.d(
                TAG,
                "Updated DiaryExerciseCrossRef: diaryId=${crossRef.diaryId}, exerciseId=${crossRef.exerciseId}"
            )

            diaryExerciseCrossRefFireStoreDataSource.updateDiaryExerciseCrossRef(crossRef)
        }
    }

    suspend fun pullFromFireStore(diaryId: String) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Pulling diary-exercise cross references from Firestore for diaryId=$diaryId")

            val crossRefs = diaryExerciseCrossRefFireStoreDataSource.getDiaryExerciseCrossRefsByDiaryId(diaryId)
            Log.d(TAG, "Pulled ${crossRefs.size} diary-exercise cross references from Firestore")

            if (crossRefs.isNotEmpty()) {
                diaryExerciseCrossRefDao.deleteAndInsertInTransaction(diaryId, crossRefs)
            }
        }
    }

    suspend fun getDiaryExerciseCrossRefByExerciseId(exerciseId: Int): List<DiaryExerciseCrossRef>? {
        return withContext(Dispatchers.IO) {
            diaryExerciseCrossRefDao.getDiaryExerciseCrossRefByExerciseId(exerciseId)
        }
    }

    suspend fun deleteDiaryExerciseCrossRef(
        userId: String,
        diaryId: String,
        exerciseId: Int
    ) {
        withContext(Dispatchers.IO) {
            diaryExerciseCrossRefDao.deleteByUserIdDiaryIdExerciseId(userId, diaryId, exerciseId)
            diaryExerciseCrossRefFireStoreDataSource.deleteByUserIdDiaryIdExerciseId(userId, diaryId, exerciseId)
        }
    }


    suspend fun pullFromFireStoreByUserId(userId: String) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Pulling diary-exercise cross references from Firestore for userId=$userId")

            val crossRefs = diaryExerciseCrossRefFireStoreDataSource.getDiaryExerciseCrossRefByUserId(userId)
            Log.d(TAG, "Pulled ${crossRefs.size} diary-exercise cross references from Firestore")

            if (crossRefs.isNotEmpty()) {
                diaryExerciseCrossRefDao.deleteAllDiaryExerciseCrossRefs()
                diaryExerciseCrossRefDao.insertAllDiaryExerciseCrossRefs(crossRefs)
            }
        }
    }

    suspend fun pullFromFireStoreByDiaryIds(diaryIds: List<String>) {
        withContext(Dispatchers.IO) {
            val allCrossRefs = diaryExerciseCrossRefFireStoreDataSource.getAllByDiaryIds(diaryIds)
            diaryExerciseCrossRefDao.deleteAllForDiaries(diaryIds)
            diaryExerciseCrossRefDao.insertAll(allCrossRefs)
        }
    }

    suspend fun insertOrUpdateDiaryExerciseCrossRef(crossRef: DiaryExerciseCrossRef) {
        withContext(Dispatchers.IO) {
            val existingCrossRef = diaryExerciseCrossRefDao.getDiaryExerciseCrossRef(crossRef.diaryId, crossRef.exerciseId)
            if (existingCrossRef != null) {
                existingCrossRef.servings += crossRef.servings
                updateDiaryExerciseCrossRef(existingCrossRef)
            } else {
                insertDiaryExerciseCrossRef(crossRef)
            }
        }
    }

}