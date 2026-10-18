package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.MediaAsset
import com.example.data.model.UserVideoMetadata
import com.example.data.model.VideoProject

@Database(entities = [VideoProject::class, MediaAsset::class, UserVideoMetadata::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun assetDao(): AssetDao
    abstract fun userVideoDao(): UserVideoDao

    fun userVideoMetadataDao(): UserVideoDao = userVideoDao()

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "omnivideo_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
