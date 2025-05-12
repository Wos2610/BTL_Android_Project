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

    suspend fun getUserProfile(userProfileId: String): UserProfile? {
        return try {
            val document = firestore.collection(COLLECTION_NAME).document(userProfileId).get().await()
            if (document.exists()) {
                mapDocumentToUserProfile(document.data)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserProfileByUserId(userId: String): UserProfile? {
        return try {
            val querySnapshot = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents[0]
                mapDocumentToUserProfile(document.data)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun insertUserProfile(userProfile: UserProfile): Result<String> {
        return try {
            val data = mapUserProfileToDocument(userProfile)
            val documentRef = firestore.collection(COLLECTION_NAME).document()
            val newUserId = documentRef.id

            val updatedData = data + mapOf("userProfileId" to newUserId)

            documentRef.set(updatedData).await()
            Result.success(newUserId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(userProfile: UserProfile): Result<Unit> {
        return try {
            val data = mapUserProfileToDocument(userProfile)
            firestore.collection(COLLECTION_NAME).document(userProfile.userProfileId)
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
            val userProfile = UserProfile(
                userProfileId = it["userProfileId"] as? String ?: "",
                userId = it["userId"] as? String ?: "",
                height = (it["height"] as? Double)?.toFloat() ?: 0f,
                currentWeight = (it["currentWeight"] as? Double)?.toFloat() ?: 0f,
                initialWeight = (it["initialWeight"] as? Double)?.toFloat() ?: 0f,
                weightGoal = (it["weightGoal"] as? Double)?.toFloat() ?: 0f,
                waterGoal = (it["waterGoal"] as? Long)?.toInt() ?: 0,
                calorieGoal = (it["calorieGoal"] as? Long)?.toInt() ?: 0,
            )
            return userProfile
        }
    }

    private fun mapUserProfileToDocument(userProfile: UserProfile): Map<String, Any> {
        return mapOf(
            "userId" to userProfile.userId,
            "height" to userProfile.height,
            "currentWeight" to userProfile.currentWeight,
            "initialWeight" to userProfile.initialWeight,
            "weightGoal" to userProfile.weightGoal,
            "waterGoal" to userProfile.waterGoal,
            "calorieGoal" to userProfile.calorieGoal,
            "userProfileId" to userProfile.userProfileId
        )
    }
}