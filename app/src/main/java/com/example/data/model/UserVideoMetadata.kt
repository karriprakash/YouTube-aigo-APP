package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Room Database entity storing metadata for user-created videos.
 * Contains local file paths, creation/update timestamps, project titles,
 * and media specifications to support offline video management.
 */
@Entity(tableName = "user_video_metadata")
data class UserVideoMetadata(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "project_title")
    val projectTitle: String,

    @ColumnInfo(name = "local_file_path")
    val localFilePath: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "duration_ms")
    val durationMs: Long = 0L,

    @ColumnInfo(name = "file_size_bytes")
    val fileSizeBytes: Long = 0L,

    @ColumnInfo(name = "resolution")
    val resolution: String = "1080x1920",

    @ColumnInfo(name = "aspect_ratio")
    val aspectRatio: String = "9:16",

    @ColumnInfo(name = "mime_type")
    val mimeType: String = "video/mp4",

    @ColumnInfo(name = "thumbnail_path")
    val thumbnailPath: String? = null,

    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false,

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "tags")
    val tags: String? = null
) {
    val formattedDuration: String
        get() {
            val totalSeconds = (durationMs / 1000).toInt()
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }

    val formattedDate: String
        get() {
            val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
            return sdf.format(Date(createdAt))
        }

    val formattedFileSize: String
        get() {
            if (fileSizeBytes <= 0) return "0 MB"
            val mb = fileSizeBytes / (1024.0 * 1024.0)
            return if (mb < 1.0) {
                val kb = fileSizeBytes / 1024.0
                String.format(Locale.getDefault(), "%.1f KB", kb)
            } else {
                String.format(Locale.getDefault(), "%.1f MB", mb)
            }
        }
}
