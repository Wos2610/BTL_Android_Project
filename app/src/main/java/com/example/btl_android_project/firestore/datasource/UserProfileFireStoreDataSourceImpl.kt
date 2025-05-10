package com.example.btl_android_project.firestore.datasource

import com.example.btl_android_project.local.entity.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserProfileFireStoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    companion object {
        private const val COLLECTION_NAME = "user_profiles"
    }

    suspend fun getUserProfile(userId: String): UserProfile? {
        return try {
            val document = firestore.collection(COLLECTION_NAME).document(userId).get().await()
            if (document.exists()) {
                mapDocumentToUserProfile(document.data)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun insertUserProfile(userProfile: UserProfile): Result<Unit> {
        return try {
            val data = mapUserProfileToDocument(userProfile)
            firestore.collection(COLLECTION_NAME).document(userProfile.userId)
                .set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(userProfile: UserProfile): Result<Unit> {
        return try {
            val data = mapUserProfileToDocument(userProfile)
            firestore.collection(COLLECTION_NAME).document(userProfile.userId)
                .update(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapSnapshotToUserProfile(snapshot: QuerySnapshot): List<UserProfile> {
        return snapshot.documents.mapNotNull { document ->
            val data = document.data
            if (data != null) {
                return@mapNotNull mapDocumentToUserProfile(data)
            } else {
                null
            }
        }
    }

    private fun mapDocumentToUserProfile(data: Map<String, Any>?): UserProfile? {
        return data?.let {
            UserProfile(
                userId = it["userId"] as? String ?: "",
                height = it["height"] as? Float ?: 0f,
                currentWeight = it["currentWeight"] as? Float ?: 0f,
                initialWeight = it["initialWeight"] as? Float ?: 0f,
                weeklyGoal = it["weeklyGoal"] as? String ?: "",
                weightGoal = it["weightGoal"] as? Float ?: 0f,
                waterGoal = it["waterGoal"] as? Int ?: 0,
                calorieGoal = it["calorieGoal"] as? Int ?: 0
            )
        }
    }

    private fun mapUserProfileToDocument(userProfile: UserProfile): Map<String, Any> {
        return mapOf(
            "userId" to userProfile.userId,
            "height" to userProfile.height,
            "currentWeight" to userProfile.currentWeight,
            "initialWeight" to userProfile.initialWeight,
            "weeklyGoal" to userProfile.weeklyGoal,
            "weightGoal" to userProfile.weightGoal,
            "waterGoal" to userProfile.waterGoal,
            "calorieGoal" to userProfile.calorieGoal
        )
    }
}