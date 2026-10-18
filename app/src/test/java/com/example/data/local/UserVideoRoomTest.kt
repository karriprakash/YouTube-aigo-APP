package com.example.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.UserVideoMetadata
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class UserVideoRoomTest {

    private lateinit var db: AppDatabase
    private lateinit var userVideoDao: UserVideoDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userVideoDao = db.userVideoDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndRetrieveUserVideoMetadata() = runBlocking {
        val now = System.currentTimeMillis()
        val video = UserVideoMetadata(
            projectTitle = "Hyderabad Food Festival Reel",
            localFilePath = "/data/user/0/com.example/files/videos/hyd_food.mp4",
            createdAt = now,
            updatedAt = now,
            durationMs = 25_000L,
            fileSizeBytes = 30_000_000L,
            resolution = "1080x1920",
            aspectRatio = "9:16",
            mimeType = "video/mp4",
            isSynced = false,
            tags = "Food, Reel",
            notes = "Test notes"
        )

        val id = userVideoDao.insert(video)
        assertTrue(id > 0)

        val retrieved = userVideoDao.getVideoByIdOnce(id)
        assertNotNull(retrieved)
        assertEquals("Hyderabad Food Festival Reel", retrieved?.projectTitle)
        assertEquals("/data/user/0/com.example/files/videos/hyd_food.mp4", retrieved?.localFilePath)
        assertEquals(now, retrieved?.createdAt)
        assertEquals(25_000L, retrieved?.durationMs)
        assertEquals("00:25", retrieved?.formattedDuration)
        assertEquals("28.6 MB", retrieved?.formattedFileSize)
    }

    @Test
    fun testFlowQueryAndSearchByTitle() = runBlocking {
        val now = System.currentTimeMillis()
        val video1 = UserVideoMetadata(
            projectTitle = "Telugu Tech Review 2026",
            localFilePath = "/data/user/0/com.example/files/videos/tech_review.mp4",
            createdAt = now,
            durationMs = 45_000L,
            fileSizeBytes = 40_000_000L
        )
        val video2 = UserVideoMetadata(
            projectTitle = "Hyderabad Charminar Night Walk",
            localFilePath = "/data/user/0/com.example/files/videos/charminar.mp4",
            createdAt = now + 1000L,
            durationMs = 15_000L,
            fileSizeBytes = 18_000_000L
        )

        userVideoDao.insert(video1)
        userVideoDao.insert(video2)

        val allVideos = userVideoDao.getAllVideos().first()
        assertEquals(2, allVideos.size)

        val searchResults = userVideoDao.searchVideosByTitle("Charminar").first()
        assertEquals(1, searchResults.size)
        assertEquals("Hyderabad Charminar Night Walk", searchResults[0].projectTitle)

        val count = userVideoDao.getCount()
        assertEquals(2, count)

        val totalStorage = userVideoDao.getTotalStorageBytes()
        assertEquals(58_000_000L, totalStorage)
    }

    @Test
    fun testDeleteUserVideoMetadata() = runBlocking {
        val video = UserVideoMetadata(
            projectTitle = "Temporary Video",
            localFilePath = "/data/user/0/com.example/files/videos/temp.mp4",
            durationMs = 10_000L
        )
        val id = userVideoDao.insert(video)
        assertEquals(1, userVideoDao.getCount())

        userVideoDao.deleteById(id)
        assertEquals(0, userVideoDao.getCount())
    }
}
