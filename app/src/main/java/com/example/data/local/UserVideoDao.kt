package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserVideoMetadata
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for user-created video metadata in Room database.
 * Supports offline querying, reactive Flow updates, search, and storage management.
 */
@Dao
interface UserVideoDao {

    @Query("SELECT * FROM user_video_metadata ORDER BY created_at DESC")
    fun getAllVideos(): Flow<List<UserVideoMetadata>>

    @Query("SELECT * FROM user_video_metadata WHERE id = :id LIMIT 1")
    fun getVideoById(id: Long): Flow<UserVideoMetadata?>

    @Query("SELECT * FROM user_video_metadata WHERE id = :id LIMIT 1")
    suspend fun getVideoByIdOnce(id: Long): UserVideoMetadata?

    @Query("SELECT * FROM user_video_metadata WHERE local_file_path = :filePath LIMIT 1")
    suspend fun getVideoByPath(filePath: String): UserVideoMetadata?

    @Query("SELECT * FROM user_video_metadata WHERE project_title LIKE '%' || :query || '%' ORDER BY created_at DESC")
    fun searchVideosByTitle(query: String): Flow<List<UserVideoMetadata>>

    @Query("SELECT * FROM user_video_metadata WHERE is_synced = :isSynced ORDER BY created_at DESC")
    fun getVideosBySyncStatus(isSynced: Boolean): Flow<List<UserVideoMetadata>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(video: UserVideoMetadata): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(videos: List<UserVideoMetadata>): List<Long>

    @Update
    suspend fun update(video: UserVideoMetadata)

    @Delete
    suspend fun delete(video: UserVideoMetadata)

    @Query("DELETE FROM user_video_metadata WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM user_video_metadata WHERE local_file_path = :filePath")
    suspend fun deleteByFilePath(filePath: String)

    @Query("SELECT COUNT(*) FROM user_video_metadata")
    suspend fun getCount(): Int

    @Query("SELECT SUM(file_size_bytes) FROM user_video_metadata")
    suspend fun getTotalStorageBytes(): Long?

    @Query("UPDATE user_video_metadata SET is_synced = :isSynced, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, isSynced: Boolean, updatedAt: Long = System.currentTimeMillis())
}
