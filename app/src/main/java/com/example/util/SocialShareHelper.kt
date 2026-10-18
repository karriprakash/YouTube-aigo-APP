package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import com.example.data.model.VideoProject

enum class SocialPlatform(
    val displayName: String,
    val packageName: String,
    val fallbackPackageName: String? = null
) {
    YOUTUBE("YouTube", "com.google.android.youtube"),
    INSTAGRAM("Instagram", "com.instagram.android"),
    TIKTOK("TikTok", "com.zhiliaoapp.musically", "com.ss.android.ugc.trill"),
    UNIVERSAL("More Apps", "")
}

object SocialShareHelper {

    fun isAppInstalled(context: Context, packageName: String): Boolean {
        if (packageName.isEmpty()) return true
        return try {
            context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    fun shareToSocialPlatform(
        context: Context,
        platform: SocialPlatform,
        project: VideoProject,
        customShareText: String? = null
    ) {
        val shareText = customShareText ?: when (platform) {
            SocialPlatform.YOUTUBE -> buildString {
                appendLine(project.youtubeTitle ?: project.title)
                appendLine()
                appendLine(project.youtubeDescription ?: project.aiSummaryEn ?: "")
                if (!project.youtubeChapters.isNullOrBlank()) {
                    appendLine()
                    appendLine("Chapters:")
                    appendLine(project.youtubeChapters)
                }
            }
            SocialPlatform.INSTAGRAM -> buildString {
                appendLine(project.instagramCaption ?: project.title)
                if (!project.instagramHashtags.isNullOrBlank()) {
                    appendLine()
                    appendLine(project.instagramHashtags)
                }
            }
            SocialPlatform.TIKTOK -> buildString {
                if (!project.tiktokHook.isNullOrBlank()) {
                    appendLine(project.tiktokHook)
                }
                appendLine(project.tiktokCaptions ?: project.title)
            }
            SocialPlatform.UNIVERSAL -> buildString {
                appendLine("🎬 ${project.title}")
                if (!project.aiSummaryTe.isNullOrBlank()) {
                    appendLine()
                    appendLine("ముఖ్యాంశాలు (Highlights):")
                    appendLine(project.aiSummaryTe)
                }
                if (!project.aiSummaryEn.isNullOrBlank()) {
                    appendLine()
                    appendLine("Summary:")
                    appendLine(project.aiSummaryEn)
                }
            }
        }

        // Always copy the formatted text to clipboard so it is immediately available when the target social app opens
        copyToClipboard(context, "${platform.displayName} Caption", shareText)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, project.title)
            putExtra(Intent.EXTRA_TEXT, shareText)

            if (project.videoUri.startsWith("content://") || project.videoUri.startsWith("file://")) {
                try {
                    putExtra(Intent.EXTRA_STREAM, Uri.parse(project.videoUri))
                    type = "video/*"
                } catch (ignored: Exception) {}
            }
        }

        if (platform == SocialPlatform.UNIVERSAL) {
            val chooser = Intent.createChooser(intent, "Share via OmniVideo")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            return
        }

        val targetPkg = if (isAppInstalled(context, platform.packageName)) {
            platform.packageName
        } else if (platform.fallbackPackageName != null && isAppInstalled(context, platform.fallbackPackageName)) {
            platform.fallbackPackageName
        } else {
            null
        }

        if (targetPkg != null) {
            intent.setPackage(targetPkg)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                // If direct package launch fails, fallback to chooser
                val chooser = Intent.createChooser(intent, "Share to ${platform.displayName}")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
            }
        } else {
            // App is not installed, open via chooser and notify
            Toast.makeText(
                context,
                "${platform.displayName} app not detected. Caption copied! Opening app selector...",
                Toast.LENGTH_LONG
            ).show()
            val chooser = Intent.createChooser(intent, "Share to ${platform.displayName}")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        }
    }
}
