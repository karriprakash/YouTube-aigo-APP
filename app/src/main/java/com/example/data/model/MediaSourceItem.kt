package com.example.data.model

enum class MediaPlatformSource(val displayName: String, val badgeColor: Long) {
    GOOGLE_PHOTOS("Google Photos", 0xFF4285F4),
    GOOGLE_SEARCH("Google Search", 0xFF34A853),
    YOUTUBE_SHORTS("YouTube Shorts", 0xFFFF0000),
    INSTAGRAM_REELS("Instagram Reels", 0xFFE1306C),
    TIKTOK("TikTok Clips", 0xFF00F2FE),
    TWITTER_X("X (Twitter)", 0xFF1DA1F2)
}

data class MediaSourceItem(
    val id: String,
    val title: String,
    val platform: MediaPlatformSource,
    val mediaType: String, // "PHOTO" or "VIDEO_CLIP"
    val durationLabel: String,
    val resolutionLabel: String,
    val tags: List<String>,
    val gradientColors: Pair<Long, Long>,
    val iconEmoji: String,
    val description: String,
    val sampleUri: String = "content://media/$id"
)

object MediaSourcingRepository {
    val curatedClips = listOf(
        // Google Search / Web Clips
        MediaSourceItem(
            id = "gs_hyd_charminar",
            title = "Charminar Evening Glow & Hyderabad B-Roll",
            platform = MediaPlatformSource.GOOGLE_SEARCH,
            mediaType = "VIDEO_CLIP",
            durationLabel = "00:15",
            resolutionLabel = "4K 60fps",
            tags = listOf("hyderabad", "telugu", "monument", "sunset", "cinematic"),
            gradientColors = Pair(0xFFF59E0B, 0xFFB45309),
            iconEmoji = "🏛️",
            description = "Cinematic golden hour drone movement over iconic Charminar and historic bazaar."
        ),
        MediaSourceItem(
            id = "gs_tech_neon",
            title = "Futuristic Cyber AI Data Stream",
            platform = MediaPlatformSource.GOOGLE_SEARCH,
            mediaType = "VIDEO_CLIP",
            durationLabel = "00:20",
            resolutionLabel = "1080p 60fps",
            tags = listOf("tech", "ai", "coding", "neon", "future"),
            gradientColors = Pair(0xFF6366F1, 0xFF4338CA),
            iconEmoji = "💻",
            description = "High-speed optical fiber data streams and glowing blue cybernetic nodes."
        ),
        MediaSourceItem(
            id = "gs_nature_waterfall",
            title = "Araku Valley Lush Greenery & Waterfall",
            platform = MediaPlatformSource.GOOGLE_SEARCH,
            mediaType = "VIDEO_CLIP",
            durationLabel = "00:25",
            resolutionLabel = "4K HDR",
            tags = listOf("nature", "araku", "telugu", "waterfall", "calm"),
            gradientColors = Pair(0xFF10B981, 0xFF047857),
            iconEmoji = "🌿",
            description = "Serene mountain mist rolling over lush Eastern Ghats tea gardens and cascading waterfalls."
        ),
        MediaSourceItem(
            id = "gs_studio_mic",
            title = "Professional Shure SM7B Podcast Setup",
            platform = MediaPlatformSource.GOOGLE_SEARCH,
            mediaType = "PHOTO",
            durationLabel = "Still",
            resolutionLabel = "4000x3000",
            tags = listOf("podcast", "audio", "microphone", "studio", "voice"),
            gradientColors = Pair(0xFF374151, 0xFF1F2937),
            iconEmoji = "🎙️",
            description = "Warm acoustic studio lighting with boom-arm broadcast microphone and soundproofing."
        ),

        // Social Media App Integrations
        MediaSourceItem(
            id = "yt_viral_shorts_1",
            title = "Trending YouTube Shorts Telugu Tech Hook",
            platform = MediaPlatformSource.YOUTUBE_SHORTS,
            mediaType = "VIDEO_CLIP",
            durationLabel = "00:18",
            resolutionLabel = "9:16 Vertical",
            tags = listOf("youtube", "shorts", "telugu", "viral", "tech"),
            gradientColors = Pair(0xFFDC2626, 0xFF991B1B),
            iconEmoji = "▶️",
            description = "High retention fast-paced intro with dynamic zoom-ins and kinetic captions."
        ),
        MediaSourceItem(
            id = "ig_reels_aesthetic",
            title = "Instagram Reel Lo-Fi Sunset Drive",
            platform = MediaPlatformSource.INSTAGRAM_REELS,
            mediaType = "VIDEO_CLIP",
            durationLabel = "00:15",
            resolutionLabel = "9:16 Vertical",
            tags = listOf("instagram", "reels", "aesthetic", "music", "sunset"),
            gradientColors = Pair(0xFFD946EF, 0xFF86198F),
            iconEmoji = "📸",
            description = "Warm orange sunset reflection on windshield with smooth slow-motion camera drift."
        ),
        MediaSourceItem(
            id = "tt_hype_transition",
            title = "TikTok Viral Snap Zoom Transition Beat",
            platform = MediaPlatformSource.TIKTOK,
            mediaType = "VIDEO_CLIP",
            durationLabel = "00:12",
            resolutionLabel = "1080x1920",
            tags = listOf("tiktok", "transition", "beat", "hype", "creator"),
            gradientColors = Pair(0xFF06B6D4, 0xFF0891B2),
            iconEmoji = "🎵",
            description = "Punchy motion blur camera whip synced to bass drop for energetic cutaways."
        ),
        MediaSourceItem(
            id = "x_breaking_feed",
            title = "X Viral Tweet / Trend Video Cutaway",
            platform = MediaPlatformSource.TWITTER_X,
            mediaType = "PHOTO",
            durationLabel = "Graphic",
            resolutionLabel = "1200x675",
            tags = listOf("twitter", "x", "breaking", "news", "discussion"),
            gradientColors = Pair(0xFF0284C7, 0xFF0369A1),
            iconEmoji = "💬",
            description = "Verified tweet reaction layout with custom headline and viral quote card."
        )
    )

    fun searchClips(query: String, platformFilter: MediaPlatformSource? = null): List<MediaSourceItem> {
        val q = query.trim().lowercase()
        return curatedClips.filter { item ->
            val matchPlatform = platformFilter == null || item.platform == platformFilter
            val matchQuery = q.isBlank() ||
                    item.title.lowercase().contains(q) ||
                    item.description.lowercase().contains(q) ||
                    item.tags.any { it.contains(q) }
            matchPlatform && matchQuery
        }
    }
}
