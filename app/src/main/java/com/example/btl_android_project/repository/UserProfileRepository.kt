package com.example.btl_android_project.repository

import android.util.Log
import com.example.btl_android_project.firestore.datasource.UserProfileFireStoreDataSourceImpl
import com.example.btl_android_project.local.dao.UserProfileDao
import com.example.btl_android_project.local.entity.UserProfile
import com.example.btl_android_project.local.enums.ActivityLevel
import com.example.btl_android_project.local.enums.WeightGoal
import com.example.btl_android_project.presentation.user_goal.UserProfileArgument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
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
            userProfileFireStoreDataSource.getUserProfileByUserId(userId)
        }
    }

    suspend fun insertUserProfile(userProfile: UserProfile): String {
        return withContext(Dispatchers.IO) {
            val result = userProfileFireStoreDataSource.insertUserProfile(userProfile)

            result.fold(
                onSuccess = { newUserProfileId ->
                    val updatedUserProfile = userProfile.copy(userProfileId = newUserProfileId)

                    userProfileDao.insertUserProfile(updatedUserProfile)

                    newUserProfileId
                },
                onFailure = { exception ->
                    throw exception
                }
            )
        }
    }

    suspend fun upsertUserProfile(userProfile: UserProfile): String {
        return withContext(Dispatchers.IO) {
            val existingProfile = userProfileDao.getUserProfileByUserId(userProfile.userId)

            if (existingProfile != null) {
                userProfileFireStoreDataSource.updateUserProfile(userProfile).fold(
                    onSuccess = {
                        // Then update in local database
                        userProfileDao.updateUserProfile(userProfile)
                        Log.d(TAG, "Successfully updated profile in Firestore and local DB")
                    },
                    onFailure = {
                        Log.e(TAG, "Failed to update profile in Firestore", it)
                        userProfileDao.updateUserProfile(userProfile)
                        Log.d(TAG, "Updated only local DB due to Firestore failure")
                    }
                )
            } else {
                userProfileFireStoreDataSource.insertUserProfile(userProfile).fold(
                    onSuccess = {
                        userProfileDao.insertUserProfile(userProfile)
                        Log.d(TAG, "Successfully inserted profile to Firestore and local DB with ID: ${userProfile.userId}")
                    },
                    onFailure = {
                        Log.e(TAG, "Failed to insert profile to Firestore", it)
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
            userProfileFireStoreDataSource.updateUserProfile(userProfile).fold(
                onSuccess = {
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
            userProfileDao.deleteUserProfile(userProfile)
            Log.d(TAG, "Deleted profile from local DB for user ID: ${userProfile.userId}")
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

    suspend fun updateCalorieGoal(userId: String, calorieGoal: Int) {
        withContext(Dispatchers.IO) {
            val profile = userProfileDao.getUserProfileByUserId(userId)

            if (profile != null) {
                val updatedProfile = profile.copy(calorieGoal = calorieGoal)

                userProfileFireStoreDataSource.updateUserProfile(updatedProfile).fold(
                    onSuccess = {
                        userProfileDao.updateUserProfile(updatedProfile)
                        Log.d(TAG, "Successfully updated calorie goal in Firestore and local DB")
                    },
                    onFailure = {
                        Log.e(TAG, "Failed to update calorie goal in Firestore", it)
                        userProfileDao.updateUserProfile(updatedProfile)
                        Log.d(TAG, "Updated calorie goal only in local DB due to Firestore failure")
                    }
                )
            } else {
                Log.e(TAG, "Cannot update calorie goal: User profile not found for ID: $userId")
            }
        }
    }

    suspend fun updateWaterGoal(userId: String, waterGoal: Int) {
        withContext(Dispatchers.IO) {
            val profile = userProfileDao.getUserProfileByUserId(userId)

            if (profile != null) {
                val updatedProfile = profile.copy(waterGoal = waterGoal)

                userProfileFireStoreDataSource.updateUserProfile(updatedProfile).fold(
                    onSuccess = {
                        userProfileDao.updateUserProfile(updatedProfile)
                        Log.d(TAG, "Successfully updated water goal in Firestore and local DB")
                    },
                    onFailure = {
                        Log.e(TAG, "Failed to update water goal in Firestore", it)
                        userProfileDao.updateUserProfile(updatedProfile)
                        Log.d(TAG, "Updated water goal only in local DB due to Firestore failure")
                    }
                )
            } else {
                Log.e(TAG, "Cannot update water goal: User profile not found for ID: $userId")
            }
        }
    }

    suspend fun updateCurrentWeight(userId: String, weight: Float) {
        withContext(Dispatchers.IO) {
            val profile = userProfileDao.getUserProfileByUserId(userId)

            if (profile != null) {
                val updatedProfile = profile.copy(currentWeight = weight)

                userProfileFireStoreDataSource.updateUserProfile(updatedProfile).fold(
                    onSuccess = {
                        userProfileDao.updateUserProfile(updatedProfile)
                        Log.d(TAG, "Successfully updated current weight in Firestore and local DB")
                    },
                    onFailure = {
                        Log.e(TAG, "Failed to update current weight in Firestore", it)
                        userProfileDao.updateUserProfile(updatedProfile)
                        Log.d(TAG, "Updated current weight only in local DB due to Firestore failure")
                    }
                )
            } else {
                Log.e(TAG, "Cannot update current weight: User profile not found for ID: $userId")
            }
        }
    }

    suspend fun getOrCreateUserProfile(userId: String): UserProfile {
        return withContext(Dispatchers.IO) {
            val existingProfile = userProfileDao.getUserProfileByUserId(userId)

            if (existingProfile != null) {
                existingProfile
            } else {
                userProfileFireStoreDataSource.getUserProfile(userId)?.let { profile ->
                    userProfileDao.insertUserProfile(profile)
                    Log.d(TAG, "Fetched user profile from Firestore and saved to local DB")
                    profile
                } ?: run {
                    val newProfile = UserProfile(
                        userId = userId,
                        height = 0f,
                        currentWeight = 0f,
                        initialWeight = 0f,
                        weightGoal = 0f,
                        waterGoal = 2000,
                        calorieGoal = 2000
                    )

                    userProfileFireStoreDataSource.insertUserProfile(newProfile).fold(
                        onSuccess = {
                            Log.d(TAG, "Successfully created new profile in Firestore with ID: $userId")
                        },
                        onFailure = {
                            Log.e(TAG, "Failed to create new profile in Firestore", it)
                        }
                    )

                    userProfileDao.insertUserProfile(newProfile)
                    Log.d(TAG, "Created new profile in local DB")

                    newProfile
                }
            }
        }
    }

    fun calculateBMR(
        weight: Float,
        height: Float,
        age: Int,
        isFemale: Boolean,
    ): Float {
        val bmr = if (isFemale) {
            10 * weight + (6.25 * height) - (5 * age) - 161
        } else {
            10 * weight + (6.25 * height) - (5 * age) + 5
        }
        return bmr.toFloat()
    }

    fun calculateTDEE(
        bmr: Float,
        activityLevel: String,
        weightGoal: String,
    ): Float {
        val activityMultiplier = when (activityLevel) {
            ActivityLevel.NOT_ACTIVE.name -> 1.2
            ActivityLevel.LIGHT_ACTIVE.name -> 1.375
            ActivityLevel.ACTIVE.name -> 1.55
            ActivityLevel.VERY_ACTIVE.name -> 1.725
            else -> 1.2
        }

        val tdee = (bmr * activityMultiplier).toFloat()

        return when (weightGoal) {
            WeightGoal.LOSE_WEIGHT.name -> tdee - 500
            WeightGoal.MAINTAIN_WEIGHT.name -> tdee
            WeightGoal.GAIN_WEIGHT.name -> tdee + 500
            else -> tdee
        }
    }

    fun calculate(
        userProfileArgument: UserProfileArgument
    ) : UserProfileArgument {
        Log.d(TAG, "calculate: $userProfileArgument")
        val dateOfBirth = userProfileArgument.dateOfBirth
        val age = calculateAge(dateOfBirth)

        val bmr = calculateBMR(
            weight = userProfileArgument.goalWeight ?: 0f,
            height = userProfileArgument.heightCm ?: 0f,
            age = age,
            isFemale = userProfileArgument.isFeMale == true
        )

        val tdee = calculateTDEE(
            bmr = bmr,
            activityLevel = userProfileArgument.activityLevel ?: ActivityLevel.NOT_ACTIVE.name,
            weightGoal = userProfileArgument.weightGoal ?: WeightGoal.MAINTAIN_WEIGHT.name
        )

        val intake = calculateComprehensiveIntake(
            weightKg = userProfileArgument.goalWeight ?: 0f,
            heightCm = userProfileArgument.heightCm ?: 0f,
            age = age,
            isFemale = userProfileArgument.isFeMale == true,
            activityLevel = userProfileArgument.activityLevel ?: ActivityLevel.NOT_ACTIVE.name,
            weightGoal = userProfileArgument.weightGoal ?: WeightGoal.MAINTAIN_WEIGHT.name
        )

        val updatedProfile = userProfileArgument.copy(goalWater = intake, caloriesGoal = tdee.toInt())

        Log.d(TAG, "BMR: $bmr, TDEE: $tdee")

        Log.d(TAG, "updatedProfile: $updatedProfile")

        return updatedProfile
    }

    fun checkSuitableWeightGoal(userProfileArgument: UserProfileArgument): String {
        val heightCm = userProfileArgument.heightCm ?: return "Height must not be empty"
        val goalWeight = userProfileArgument.goalWeight ?: return "Target weight must not be empty"
        val currentWeight = userProfileArgument.currentWeight ?: return "Current weight must not be empty"
        val dateOfBirth = userProfileArgument.dateOfBirth ?: return "Date of birth must not be empty"
        val age = calculateAge(dateOfBirth)
        val weightGoal = userProfileArgument.weightGoal ?: return "Weight goal must not be empty"
        val isFemale = userProfileArgument.isFeMale == true

        val heightM = heightCm / 100f
        val targetBMI = goalWeight / (heightM * heightM)

        if (targetBMI < 18.5) {
            return "Target weight is too low. Target BMI (${String.format("%.1f", targetBMI)}) is below the healthy threshold (18.5)"
        }

        if (targetBMI > 25) {
            return "Target weight is high. Target BMI (${String.format("%.1f", targetBMI)}) is above the normal range (25)"
        }

        val weightDifference = goalWeight - currentWeight
        if (weightGoal == WeightGoal.LOSE_WEIGHT.name && weightDifference >= 0) {
            return "Invalid weight loss goal: target weight must be less than current weight"
        }

        if (weightGoal == WeightGoal.GAIN_WEIGHT.name && weightDifference <= 0) {
            return "Invalid weight gain goal: target weight must be greater than current weight"
        }

        if (weightGoal == WeightGoal.MAINTAIN_WEIGHT.name && weightDifference != 0f) {
            return "Invalid weight maintenance goal: target weight must be equal to current weight"
        }

        val safeWeightChangePerWeek = 0.5f
        val safeWeightChangePerMonth = safeWeightChangePerWeek * 4

        if (weightGoal == WeightGoal.LOSE_WEIGHT.name) {
            val minSafeWeight = currentWeight - safeWeightChangePerMonth
            if (goalWeight < minSafeWeight) {
                return "Weight loss goal is too aggressive. You should not lose more than ${safeWeightChangePerMonth}kg per month"
            }
        }

        if (weightGoal == WeightGoal.GAIN_WEIGHT.name) {
            val maxSafeWeight = currentWeight + safeWeightChangePerMonth
            if (goalWeight > maxSafeWeight) {
                return "Weight gain goal is too aggressive. You should not gain more than ${safeWeightChangePerMonth}kg per month"
            }
        }

        if (weightGoal == WeightGoal.MAINTAIN_WEIGHT.name && Math.abs(weightDifference) > 1f) {
            return "To maintain weight, the target weight should be close to current weight (±1kg)"
        }

        if (age < 18) {
            return "Setting weight goals is not recommended for individuals under 18. Please consult a doctor"
        }

        if (age > 65 && Math.abs(weightDifference) > safeWeightChangePerMonth / 2) {
            return "Weight change goal is too aggressive for individuals over 65. Gradual change is recommended and medical advice should be sought"
        }

        return "Weight goal is reasonable"
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

    fun calculateComprehensiveIntake(
        weightKg: Float,
        heightCm: Float,
        age: Int,
        isFemale: Boolean,
        activityLevel: String,
        weightGoal: String,
    ): Float {
        var baseIntake = if (isFemale) {
            (655.1f + (9.563f * weightKg) + (1.850f * heightCm) - (4.676f * age))
        } else {
            (66.5f + (13.75f * weightKg) + (5.003f * heightCm) - (6.775f * age))
        }

        baseIntake *= 0.85f

        val activityMultiplier = when (activityLevel) {
            ActivityLevel.NOT_ACTIVE.name -> 1.2f
            ActivityLevel.LIGHT_ACTIVE.name -> 1.3f
            ActivityLevel.ACTIVE.name -> 1.5f
            ActivityLevel.VERY_ACTIVE.name -> 1.7f
            else -> 1.2f
        }

        val goalMultiplier = when (weightGoal) {
            WeightGoal.LOSE_WEIGHT.name -> 1.1f
            WeightGoal.MAINTAIN_WEIGHT.name -> 1.0f
            WeightGoal.GAIN_WEIGHT.name -> 0.95f
            else -> 1.0f
        }

        return baseIntake * activityMultiplier * goalMultiplier
    }
}