package com.example.btl_android_project.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.btl_android_project.local.entity.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles")
    fun getUserProfiles(): Flow<List<UserProfile>>
    
    @Query("SELECT * FROM user_profiles WHERE userId = :userId")
    suspend fun getUserProfileByUserId(userId: String): UserProfile?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfile): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllUserProfiles(userProfiles: List<UserProfile>)
    
    @Update
    suspend fun updateUserProfile(userProfile: UserProfile): Int
    
    @Delete
    suspend fun deleteUserProfile(userProfile: UserProfile): Int
    
    @Query("DELETE FROM user_profiles")
    suspend fun deleteAllUserProfiles()
}