package com.example.btl_android_project.repository

import android.util.Log
import com.example.btl_android_project.firestore.datasource.UserProfileFireStoreDataSourceImpl
import com.example.btl_android_project.local.ActivityLevel
import com.example.btl_android_project.local.WeightGoal
import com.example.btl_android_project.local.dao.UserProfileDao
import com.example.btl_android_project.local.entity.UserProfile
import com.example.btl_android_project.presentation.user_goal.UserProfileArgument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class UserProfileRepository @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val userProfileFireStoreDataSource: UserProfileFireStoreDataSourceImpl
) {
    private val TAG = "UserProfileRepository"

    fun getUserProfiles(): Flow<List<UserProfile>> = userProfileDao.getUserProfiles()

    suspend fun getUserProfileByUserId(userId: String): UserProfile? {
        return withContext(Dispatchers.IO) {
            userProfileFireStoreDataSource.getUserProfile(userId)
        }
    }



    suspend fun upsertUserProfile(userProfile: UserProfile): String {
        return withContext(Dispatchers.IO) {
            // First, check if profile exists
            val existingProfile = userProfileDao.getUserProfileByUserId(userProfile.userId)

            if (existingProfile != null) {
                // Update existing profile in Firestore first
                userProfileFireStoreDataSource.updateUserProfile(userProfile).fold(
                    onSuccess = {
                        // Then update in local database
                        userProfileDao.updateUserProfile(userProfile)
                        Log.d(TAG, "Successfully updated profile in Firestore and local DB")
                    },
                    onFailure = {
                        Log.e(TAG, "Failed to update profile in Firestore", it)
                        // Fall back to updating only local DB
                        userProfileDao.updateUserProfile(userProfile)
                        Log.d(TAG, "Updated only local DB due to Firestore failure")
                    }
                )
            } else {
                // Insert new profile to Firestore first
                userProfileFireStoreDataSource.insertUserProfile(userProfile).fold(
                    onSuccess = {
                        // Then insert to local database
                        userProfileDao.insertUserProfile(userProfile)
                        Log.d(TAG, "Successfully inserted profile to Firestore and local DB with ID: ${userProfile.userId}")
                    },
                    onFailure = {
                        Log.e(TAG, "Failed to insert profile to Firestore", it)
                        // Fall back to inserting only to local DB
                        userProfileDao.insertUserProfile(userProfile)
                        Log.d(TAG, "Inserted only to local DB due to Firestore failure")
                    }
                )
            }

            userProfile.userId
        }
    }

    suspend fun updateUserProfile(userProfile: UserProfile) {
        withContext(Dispatchers.IO) {
            // Update in Firestore first
            userProfileFireStoreDataSource.updateUserProfile(userProfile).fold(
                onSuccess = {
                    // Then update local database
                    userProfileDao.updateUserProfile(userProfile)
                    Log.d(TAG, "Successfully updated profile in Firestore and local DB")
                },
                onFailure = {
                    Log.e(TAG, "Failed to update profile in Firestore", it)
                }
            )
        }
    }

    suspend fun deleteUserProfile(userProfile: UserProfile) {
        withContext(Dispatchers.IO) {
            // Delete from local database
            userProfileDao.deleteUserProfile(userProfile)
            Log.d(TAG, "Deleted profile from local DB for user ID: ${userProfile.userId}")

            // Note: Should implement Firestore deletion in FireStoreDataSourceImpl if needed
        }
    }

    suspend fun pullFromFireStore(userId: String) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Pulling user profile from Firestore for user ID: $userId")

            userProfileFireStoreDataSource.getUserProfile(userId)?.let { profile ->
                userProfileDao.insertUserProfile(profile)
                Log.d(TAG, "Successfully pulled and inserted user profile from Firestore")
            } ?: run {
                Log.d(TAG, "No user profile found in Firestore for user ID: $userId")
            }
        }
    }

    /**
     * Update calorie goal for a user
     * Modified to update Firestore first, then local database
     */
    suspend fun updateCalorieGoal(userId: String, calorieGoal: Int) {
        withContext(Dispatchers.IO) {
            val profile = userProfileDao.getUserProfileByUserId(userId)

            if (profile != null) {
                val updatedProfile = profile.copy(calorieGoal = calorieGoal)

                // Update in Firestore first
                userProfileFireStoreDataSource.updateUserProfile(updatedProfile).fold(
                    onSuccess = {
                        // Then update local database
                        userProfileDao.updateUserProfile(updatedProfile)
                        Log.d(TAG, "Successfully updated calorie goal in Firestore and local DB")
                    },
                    onFailure = {
                        Log.e(TAG, "Failed to update calorie goal in Firestore", it)
                        // Fall back to updating only local DB
                        userProfileDao.updateUserProfile(updatedProfile)
                        Log.d(TAG, "Updated calorie goal only in local DB due to Firestore failure")
                    }
                )
            } else {
                Log.e(TAG, "Cannot update calorie goal: User profile not found for ID: $userId")
            }
        }
    }

    /**
     * Update water goal for a user
     * Modified to update Firestore first, then local database
     */
    suspend fun updateWaterGoal(userId: String, waterGoal: Int) {
        withContext(Dispatchers.IO) {
            val profile = userProfileDao.getUserProfileByUserId(userId)

            if (profile != null) {
                val updatedProfile = profile.copy(waterGoal = waterGoal)

                // Update in Firestore first
                userProfileFireStoreDataSource.updateUserProfile(updatedProfile).fold(
                    onSuccess = {
                        // Then update local database
                        userProfileDao.updateUserProfile(updatedProfile)
                        Log.d(TAG, "Successfully updated water goal in Firestore and local DB")
                    },
                    onFailure = {
                        Log.e(TAG, "Failed to update water goal in Firestore", it)
                        // Fall back to updating only local DB
                        userProfileDao.updateUserProfile(updatedProfile)
                        Log.d(TAG, "Updated water goal only in local DB due to Firestore failure")
                    }
                )
            } else {
                Log.e(TAG, "Cannot update water goal: User profile not found for ID: $userId")
            }
        }
    }

    /**
     * Update current weight for a user
     * Modified to update Firestore first, then local database
     */
    suspend fun updateCurrentWeight(userId: String, weight: Float) {
        withContext(Dispatchers.IO) {
            val profile = userProfileDao.getUserProfileByUserId(userId)

            if (profile != null) {
                val updatedProfile = profile.copy(currentWeight = weight)

                // Update in Firestore first
                userProfileFireStoreDataSource.updateUserProfile(updatedProfile).fold(
                    onSuccess = {
                        // Then update local database
                        userProfileDao.updateUserProfile(updatedProfile)
                        Log.d(TAG, "Successfully updated current weight in Firestore and local DB")
                    },
                    onFailure = {
                        Log.e(TAG, "Failed to update current weight in Firestore", it)
                        // Fall back to updating only local DB
                        userProfileDao.updateUserProfile(updatedProfile)
                        Log.d(TAG, "Updated current weight only in local DB due to Firestore failure")
                    }
                )
            } else {
                Log.e(TAG, "Cannot update current weight: User profile not found for ID: $userId")
            }
        }
    }

    /**
     * Get or create a user profile
     * Modified to create in Firestore first, then in local database
     */
    suspend fun getOrCreateUserProfile(userId: String): UserProfile {
        return withContext(Dispatchers.IO) {
            val existingProfile = userProfileDao.getUserProfileByUserId(userId)

            if (existingProfile != null) {
                existingProfile
            } else {
                // Try to fetch from Firestore first
                userProfileFireStoreDataSource.getUserProfile(userId)?.let { profile ->
                    userProfileDao.insertUserProfile(profile)
                    Log.d(TAG, "Fetched user profile from Firestore and saved to local DB")
                    profile
                } ?: run {
                    // Create a new default profile if not found anywhere
                    val newProfile = UserProfile(
                        userId = userId,
                        height = 0f,
                        currentWeight = 0f,
                        initialWeight = 0f,
                        weeklyGoal = "",
                        weightGoal = 0f,
                        waterGoal = 2000, // Default 2 liters
                        calorieGoal = 2000 // Default 2000 calories
                    )

                    // Insert to Firestore first
                    userProfileFireStoreDataSource.insertUserProfile(newProfile).fold(
                        onSuccess = {
                            Log.d(TAG, "Successfully created new profile in Firestore with ID: $userId")
                        },
                        onFailure = {
                            Log.e(TAG, "Failed to create new profile in Firestore", it)
                        }
                    )

                    // Then insert to local database
                    userProfileDao.insertUserProfile(newProfile)
                    Log.d(TAG, "Created new profile in local DB")

                    newProfile
                }
            }
        }
    }

    fun calculateBMR(
        weight: Double,
        height: Double,
        age: Int,
        isFemale: Boolean,
    ): Double {
        val bmr = if (isFemale) {
            10 * weight + (6.25 * height) - (5 * age) - 161
        } else {
            10 * weight + (6.25 * height) - (5 * age) + 5
        }
        return bmr
    }

    fun calculateTDEE(
        bmr: Double,
        activityLevel: String,
        weightGoal: String,
    ): Double {
        val activityMultiplier = when (activityLevel) {
            ActivityLevel.NOT_ACTIVE.name -> 1.2
            ActivityLevel.LIGHT_ACTIVE.name -> 1.375
            ActivityLevel.ACTIVE.name -> 1.55
            ActivityLevel.VERY_ACTIVE.name -> 1.725
            else -> 1.2
        }

        val tdee = bmr * activityMultiplier

        return when (weightGoal) {
            WeightGoal.LOSE_WEIGHT.name -> tdee - 500
            WeightGoal.MAINTAIN_WEIGHT.name -> tdee
            WeightGoal.GAIN_WEIGHT.name -> tdee + 500
            else -> tdee
        }
    }

    fun calculate(
        userProfileArgument: UserProfileArgument
    ){
        Log.d(TAG, "calculate: $userProfileArgument")
        val dateOfBirth = userProfileArgument.dateOfBirth
        val age = calculateAge(dateOfBirth)

        val bmr = calculateBMR(
            weight = userProfileArgument.goalWeight ?: 0.0,
            height = userProfileArgument.heightCm ?: 0.0,
            age = age,
            isFemale = userProfileArgument.isFeMale == true
        )

        val tdee = calculateTDEE(
            bmr = bmr,
            activityLevel = userProfileArgument.activityLevel ?: ActivityLevel.NOT_ACTIVE.name,
            weightGoal = userProfileArgument.weightGoal ?: WeightGoal.MAINTAIN_WEIGHT.name
        )

        Log.d(TAG, "BMR: $bmr, TDEE: $tdee")
    }

    fun calculateAge(dateOfBirth: String?): Int {
        return if (dateOfBirth != null) {
            try {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
                val birthDate = LocalDate.parse(dateOfBirth, formatter)
                val today = LocalDate.now()
                Period.between(birthDate, today).years
            } catch (e: Exception) {
                0
            }
        } else {
            0
        }
    }
}