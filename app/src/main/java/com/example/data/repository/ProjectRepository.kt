package com.example.data.repository

import com.example.data.local.AssetDao
import com.example.data.local.ProjectDao
import com.example.data.model.MediaAsset
import com.example.data.model.VideoProject
import kotlinx.coroutines.flow.Flow

class ProjectRepository(
    private val projectDao: ProjectDao,
    private val assetDao: AssetDao
) {
    val allProjects: Flow<List<VideoProject>> = projectDao.getAllProjects()
    val allAssets: Flow<List<MediaAsset>> = assetDao.getAllAssets()

    fun getProjectById(id: Long): Flow<VideoProject?> = projectDao.getProjectById(id)

    suspend fun getProjectByIdOnce(id: Long): VideoProject? = projectDao.getProjectByIdOnce(id)

    suspend fun insertProject(project: VideoProject): Long = projectDao.insert(project)

    suspend fun updateProject(project: VideoProject) = projectDao.update(project)

    suspend fun deleteProject(project: VideoProject) = projectDao.delete(project)

    suspend fun deleteProjectById(id: Long) = projectDao.deleteById(id)

    suspend fun insertAsset(asset: MediaAsset): Long = assetDao.insert(asset)

    suspend fun deleteAsset(asset: MediaAsset) = assetDao.delete(asset)

    suspend fun seedInitialDataIfEmpty() {
        if (projectDao.getCount() == 0) {
            val sample1 = VideoProject(
                title = "హైదరాబాద్ బిర్యానీ సీక్రెట్స్ (Food Vlog)",
                videoUri = "asset://sample_biryani_reel.mp4",
                audioUri = "asset://telugu_voice_audio.mp3",
                originalDurationMs = 28_000L,
                trimStartMs = 2_000L,
                trimEndMs = 22_000L,
                aspectRatio = "9:16",
                colorFilter = "VIBRANT",
                filterIntensity = 0.85f,
                originalAudioVolume = 0.7f,
                isOriginalAudioMuted = false,
                voiceoverVolume = 1.2f,
                voiceClarityEnhanced = true,
                overlayText = "🔥 హైదరాబాదీ దమ్ బిర్యానీ సీక్రెట్!",
                overlayTextColorHex = "#F59E0B",
                overlayTextBgStyle = "PILL",
                overlayTextPosition = "BOTTOM",
                overlayTextSize = 22,
                language = "te",
                rawTopicNotes = "Authentic wood-fire dum biryani preparation in Old City Hyderabad with aromatic spices, basmati rice layering, and saffron milk.",
                aiSummaryEn = "• Authentic secret recipe of Hyderabadi Dum Biryani cooked on slow wood fire.\n• Key spice layering technique for soft meat and perfectly separated basmati rice grains.\n• Saffron infused ghee finish that creates signature royal aroma.",
                aiSummaryTe = "• పాతబస్తీ సంప్రదాయ కట్టెల పొయ్యిపై తయారుచేసిన అసలైన హైదరాబాదీ దమ్ బిర్యానీ రహస్యాలు.\n• మాంసం మెత్తగా ఉడకడానికి మరియు బాస్మతి బియ్యం విడివిడిగా రావడానికి ఉపయోగించే ప్రత్యేక మసాలా మిశ్రమం.\n• బిర్యానీకి రాయల్ సువాసనను అందించే కుంకుమపువ్వు, నెయ్యి కాంబినేషన్.",
                youtubeTitle = "హైదరాబాదీ దమ్ బిర్యానీ సీక్రెట్ రెసిపీ | Best Hyderabadi Biryani Making",
                youtubeDescription = "Learn how real Hyderabadi Dum Biryani is cooked in traditional degs! Full step-by-step breakdown with secret spice ratios.\n\nTimestamps:\n00:00 - Intro & Meat Marinade\n00:06 - Rice Boiling 70% Technique\n00:14 - Layering & Dum Process\n00:19 - Final Tasting & Serving",
                youtubeChapters = "00:00 Intro & Marinade\n00:06 Rice Technique\n00:14 Dum Steaming\n00:19 Reveal",
                instagramCaption = "పాతబస్తీ స్టైల్ అసలైన దమ్ బిర్యానీ రుచి చూశారా? 🤤🔥 Secret spice ratio in this reel! Save this for your Sunday feast.\n\nDrop a ❤️ if you love Biryani!",
                instagramHashtags = "#HyderabadiBiryani #TeluguFoodie #BiryaniLovers #StreetFoodHyderabad #TeluguReels #FoodPorn #InstaFood #FoodVlog #FoodShorts",
                tiktokHook = "Stop cooking biryani wrong! Here is the authentic Hyderabad method:",
                tiktokCaptions = "Secret dum technique 🍚🔥 #biryani #foodtok #hyderabad #indianfood #recipe #easycooking",
                brollSuggestions = "00:00 Close-up raw saffron drop\n00:05 Steam rising from sealed deg pot\n00:15 Slow motion rice spoon lift",
                isExported = true
            )

            val sample2 = VideoProject(
                title = "AI Smart Gadgets 2026 (టెక్ రివ్యూ)",
                videoUri = "asset://sample_ai_gadget.mp4",
                audioUri = null,
                originalDurationMs = 45_000L,
                trimStartMs = 0L,
                trimEndMs = 35_000L,
                aspectRatio = "16:9",
                colorFilter = "CINEMATIC",
                filterIntensity = 0.9f,
                originalAudioVolume = 1.0f,
                isOriginalAudioMuted = false,
                voiceoverVolume = 1.0f,
                voiceClarityEnhanced = true,
                overlayText = "⚡ Top 3 AI Gadgets You Need in 2026",
                overlayTextColorHex = "#06B6D4",
                overlayTextBgStyle = "BANNER",
                overlayTextPosition = "TOP",
                overlayTextSize = 20,
                language = "te",
                rawTopicNotes = "Hands-on review of wearable AI translation ring and holographic pocket projector with real-time Telugu voice translation.",
                aiSummaryEn = "• Wearable AI ring provides instant two-way voice translation between Telugu and English.\n• Ultra-lightweight build with 4-day battery life and neural gesture controls.\n• Affordable flagship alternative for content creators and travelers.",
                aiSummaryTe = "• తెలుగు మరియు ఇంగ్లీష్ మధ్య రియల్-టైమ్ వాయిస్ అనువాదం చేసే స్మార్ట్ AI రింగ్ రివ్యూ.\n• 4 రోజుల బ్యాటరీ లైఫ్ మరియు ఆధునిక గెస్చర్ కంట్రోల్స్ కలిగిన సరికొత్త పరికరం.\n• కంటెంట్ క్రియేటర్స్ మరియు ప్రయాణికులకు బెస్ట్ గ్యాడ్జెట్.",
                youtubeTitle = "2026 లో టాప్ 3 AI గ్యాడ్జెట్స్! | Best Future AI Tech You Can Buy",
                youtubeDescription = "Mind blowing future tech is here! We tested the real-time multilingual AI voice translator ring and micro-projector.\n\nChapters:\n00:00 - Wearable AI Ring\n00:12 - Realtime Telugu Voice Test\n00:24 - Pocket Hologram Display\n00:32 - Final Verdict & Price",
                youtubeChapters = "00:00 AI Ring\n00:12 Telugu Voice Test\n00:24 Micro Projector\n00:32 Verdict",
                instagramCaption = "ఈ చిన్న రింగ్ తెలుగు మాటల్ని ఇంగ్లీష్‌లోకి సెకన్లలో మారుస్తుంది! 🤖⚡ Future tech is already here. Tag a tech lover who needs this!",
                instagramHashtags = "#TeluguTech #AIGadgets #FutureTech #TechReview #TeluguShorts #SmartDevices #AIInnovation #GadgetLovers",
                tiktokHook = "This $49 gadget can translate Telugu to English in real time live:",
                tiktokCaptions = "Future AI ring review ⚡ #techtok #ai #gadgets #cooltech #techguide",
                brollSuggestions = "00:00 Macro shot of the AI ring sensor\n00:10 Glowing indicator light during voice input\n00:20 Split screen before/after translation",
                isExported = false
            )

            val sample3 = VideoProject(
                title = "చేనేత చీరల కళ (Weaving Heritage)",
                videoUri = "asset://sample_handloom_loom.mp4",
                audioUri = "asset://telugu_flute_bgm.mp3",
                originalDurationMs = 24_000L,
                trimStartMs = 1_500L,
                trimEndMs = 20_000L,
                aspectRatio = "1:1",
                colorFilter = "VINTAGE",
                filterIntensity = 0.75f,
                originalAudioVolume = 0.4f,
                isOriginalAudioMuted = false,
                voiceoverVolume = 1.3f,
                voiceClarityEnhanced = true,
                overlayText = "సంప్రదాయ చేనేత అందం ✨",
                overlayTextColorHex = "#FFFFFF",
                overlayTextBgStyle = "PILL",
                overlayTextPosition = "CENTER",
                overlayTextSize = 24,
                language = "te",
                rawTopicNotes = "Traditional Dharmavaram silk handloom weaving, capturing rhythmic shuttle movement, natural dye threads, and artisan dedication.",
                aiSummaryEn = "• Documenting generation-old Dharmavaram silk handloom craftsmanship.\n• Complex mathematical patterns woven manually thread by thread.\n• Promoting sustainable native handloom fashion.",
                aiSummaryTe = "• తరతరాల ధర్మవరం పట్టు చేనేత కళాకారుల ప్రతిభ మరియు శ్రమ.\n• ప్రతి దారాన్ని చేతులతో నేసి అద్భుతమైన డిజైన్లను రూపొందించే విధానం.\n• స్వదేశీ చేనేత వస్త్రాల ప్రాముఖ్యత మరియు వాటి సాంస్కృతిక విలువ.",
                youtubeTitle = "ధర్మవరం పట్టు చేనేత చీరలు ఎలా నేస్తారు? | Traditional Handloom Documentary",
                youtubeDescription = "Inside the workshops of master silk weavers in Andhra Pradesh.\n\nChapters:\n00:00 - The Loom Rhythm\n00:08 - Pure Zari & Silk Threading\n00:16 - Finished Masterpiece",
                youtubeChapters = "00:00 Loom Rhythm\n00:08 Silk Threads\n00:16 Masterpiece",
                instagramCaption = "ఒక్క చీర నేయడానికి 15 రోజుల కఠిన శ్రమ! ✨ Support Indian Handloom weavers. Spread the love for our heritage.\n\nShare this with someone who appreciates craftsmanship! 🌿",
                instagramHashtags = "#Handloom #TeluguCulture #DharmavaramSilk #VocalForLocal #ArtisansOfIndia #SareeLove #TraditionalWeaving #DesiVibes",
                tiktokHook = "Did you know one authentic silk saree takes 15 days of manual weaving?",
                tiktokCaptions = "Ancient handloom art alive in India 🧵✨ #satisfying #handloom #craftsmanship #tradition #handmade",
                brollSuggestions = "00:00 Flying wooden shuttle slow-mo\n00:07 Thread tension wheel spinning\n00:15 Golden zari border shimmer in sunlight",
                isExported = true
            )

            projectDao.insert(sample1)
            projectDao.insert(sample2)
            projectDao.insert(sample3)
        }

        if (assetDao.getCount() == 0) {
            val asset1 = MediaAsset(
                name = "Charminar_Street_Food_Raw.mp4",
                type = "VIDEO",
                uri = "asset://raw_food_clip.mp4",
                durationMs = 28_000L,
                language = "te",
                description = "Raw 4K camera footage of steaming food counter in Hyderabad",
                sampleTopic = "Hyderabad Street Food Tasting"
            )
            val asset2 = MediaAsset(
                name = "Smart_Gadget_Broll.mp4",
                type = "VIDEO",
                uri = "asset://raw_tech_clip.mp4",
                durationMs = 45_000L,
                language = "en",
                description = "Macro studio lighting b-roll of wearable tech device",
                sampleTopic = "Next-Gen AI Hardware Unboxing"
            )
            val asset3 = MediaAsset(
                name = "Telugu_Narration_Audio.m4a",
                type = "VOICEOVER",
                uri = "asset://telugu_narration.m4a",
                durationMs = 30_000L,
                language = "te",
                description = "Clear studio recorded Telugu vocal commentary with expressive intonation",
                sampleTopic = "Cultural Travel Story"
            )
            val asset4 = MediaAsset(
                name = "Cinematic_Ambient_Flute.mp3",
                type = "AUDIO",
                uri = "asset://ambient_flute.mp3",
                durationMs = 60_000L,
                language = "te",
                description = "Peaceful acoustic flute and percussion backing track",
                sampleTopic = "Background Music"
            )

            assetDao.insert(asset1)
            assetDao.insert(asset2)
            assetDao.insert(asset3)
            assetDao.insert(asset4)
        }
    }
}
