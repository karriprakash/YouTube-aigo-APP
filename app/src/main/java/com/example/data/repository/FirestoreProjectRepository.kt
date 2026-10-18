package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.CloudVideoProject
import com.example.firebase.OperationType
import com.example.firebase.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreProjectRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    // Secondary constructor resolving named database ID from string resources
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    private fun projectsCollection(userId: String) =
        db.collection("users").document(userId).collection("projects")

    fun observeUserProjects(): Flow<List<CloudVideoProject>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/projects"
        projectsCollection(uid)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(CloudVideoProject::class.java) }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
            .collect { emit(it) }
    }

    suspend fun saveProject(project: CloudVideoProject, projectId: String): String {
        val uid = requireUserId()
        val docRef = projectsCollection(uid).document(projectId)
        val path = docRef.path
        try {
            val payload = hashMapOf<String, Any?>(
                "userId" to uid,
                "title" to project.title,
                "videoUri" to project.videoUri,
                "audioUri" to project.audioUri,
                "originalDurationMs" to project.originalDurationMs,
                "trimStartMs" to project.trimStartMs,
                "trimEndMs" to project.trimEndMs,
                "aspectRatio" to project.aspectRatio,
                "colorFilter" to project.colorFilter,
                "filterIntensity" to project.filterIntensity,
                "originalAudioVolume" to project.originalAudioVolume,
                "isOriginalAudioMuted" to project.isOriginalAudioMuted,
                "voiceoverVolume" to project.voiceoverVolume,
                "voiceClarityEnhanced" to project.voiceClarityEnhanced,
                "overlayText" to project.overlayText,
                "overlayTextColorHex" to project.overlayTextColorHex,
                "overlayTextBgStyle" to project.overlayTextBgStyle,
                "overlayTextPosition" to project.overlayTextPosition,
                "overlayTextSize" to project.overlayTextSize,
                "language" to project.language,
                "rawTopicNotes" to project.rawTopicNotes,
                "aiSummaryEn" to project.aiSummaryEn,
                "aiSummaryTe" to project.aiSummaryTe,
                "youtubeTitle" to project.youtubeTitle,
                "youtubeDescription" to project.youtubeDescription,
                "youtubeChapters" to project.youtubeChapters,
                "instagramCaption" to project.instagramCaption,
                "instagramHashtags" to project.instagramHashtags,
                "tiktokHook" to project.tiktokHook,
                "tiktokCaptions" to project.tiktokCaptions,
                "brollSuggestions" to project.brollSuggestions,
                "isExported" to project.isExported,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null }

            docRef.set(payload).await()
            return projectId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            throw e
        }
    }

    suspend fun deleteProject(projectId: String) {
        val uid = requireUserId()
        val docRef = projectsCollection(uid).document(projectId)
        val path = docRef.path
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            throw e
        }
    }
}
