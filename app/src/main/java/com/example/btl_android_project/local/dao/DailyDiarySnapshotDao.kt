package com.example.btl_android_project.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.btl_android_project.local.entity.DailyDiarySnapshot
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyDiarySnapshotDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnapshot(snapshot: DailyDiarySnapshot): Long

    @Query("SELECT * FROM daily_diary_snapshots WHERE userId = :userId ORDER BY logDate DESC")
    fun getAllSnapshots(userId: String): Flow<List<DailyDiarySnapshot>>

    @Query("SELECT * FROM daily_diary_snapshots WHERE diaryId = :diaryId")
    suspend fun getSnapshotForDiary(diaryId: String): DailyDiarySnapshot?

    @Query("SELECT * FROM daily_diary_snapshots WHERE syncedToFirestore = 0")
    suspend fun getUnsyncedSnapshots(): List<DailyDiarySnapshot>

    @Update
    suspend fun updateSnapshot(snapshot: DailyDiarySnapshot)

    @Query("UPDATE daily_diary_snapshots SET syncedToFirestore = 1 WHERE id = :snapshotId")
    suspend fun markAsSynced(snapshotId: String)
}