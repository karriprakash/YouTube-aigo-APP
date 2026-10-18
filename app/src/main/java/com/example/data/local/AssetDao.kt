package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.MediaAsset
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {
    @Query("SELECT * FROM media_assets ORDER BY dateAdded DESC")
    fun getAllAssets(): Flow<List<MediaAsset>>

    @Query("SELECT * FROM media_assets WHERE type = :type ORDER BY dateAdded DESC")
    fun getAssetsByType(type: String): Flow<List<MediaAsset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(asset: MediaAsset): Long

    @Delete
    suspend fun delete(asset: MediaAsset)

    @Query("DELETE FROM media_assets WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM media_assets")
    suspend fun getCount(): Int
}
