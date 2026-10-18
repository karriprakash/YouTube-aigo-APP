package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.CloudVideoProject
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class FirestoreProjectRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun authenticatedUserCanSaveAndRetrieveProject(): Unit = runBlocking {
        val aliceUid = signInTestUser("alice@example.com")
        val repo = FirestoreProjectRepository(firestore, auth)

        val project = CloudVideoProject(
            userId = aliceUid,
            title = "Alice Telugu Food Vlog",
            videoUri = "asset://biryani.mp4",
            originalDurationMs = 28_000L,
            trimStartMs = 1_000L,
            trimEndMs = 22_000L,
            aspectRatio = "9:16",
            language = "te"
        )

        val savedId = repo.saveProject(project, "proj_alice_123")
        assertEquals("proj_alice_123", savedId)

        val projectsList = repo.observeUserProjects().first()
        assertTrue("Projects list should contain saved project", projectsList.any { it.title == "Alice Telugu Food Vlog" })
    }

    @Test
    fun unauthenticatedUserCannotSaveProject(): Unit = runBlocking {
        auth.signOut()
        val repo = FirestoreProjectRepository(firestore, auth)

        val project = CloudVideoProject(
            userId = "anon_123",
            title = "Unauthorized Project",
            videoUri = "asset://bad.mp4"
        )

        try {
            repo.saveProject(project, "proj_fail")
            fail("Expected IllegalStateException when unauthenticated")
        } catch (e: IllegalStateException) {
            assertTrue(e.message?.contains("User must be signed in") == true)
        }
    }

    @Test
    fun crossUserAccessIsRejectedBySecurityRules(): Unit = runBlocking {
        // Sign in Bob and save a project
        val bobUid = signInTestUser("bob@example.com")
        val bobRepo = FirestoreProjectRepository(firestore, auth)

        val bobProject = CloudVideoProject(
            userId = bobUid,
            title = "Bob Tech Review",
            videoUri = "asset://tech.mp4"
        )
        bobRepo.saveProject(bobProject, "bob_project_99")

        // Switch to Alice
        val aliceUid = signInTestUser("alice2@example.com")
        val aliceRepo = FirestoreProjectRepository(firestore, auth)

        // Alice attempts to access Bob's collection directly
        try {
            firestore.collection("users").document(bobUid).collection("projects").document("bob_project_99").get().await()
            fail("Expected PERMISSION_DENIED on cross-user read")
        } catch (e: Exception) {
            // PERMISSION_DENIED or FirebaseFirestoreException expected
            assertNotNull(e)
        }
    }
}
