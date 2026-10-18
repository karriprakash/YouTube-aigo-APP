package com.example.data.model

data class MemeTemplate(
    val id: String,
    val name: String,
    val category: String, // "Trending", "Telugu Classics", "Reactions", "Gaming & Tech"
    val defaultTopText: String,
    val defaultBottomText: String,
    val emoji: String,
    val bgGradientHex: Pair<Long, Long>,
    val punchlineTe: String = "",
    val description: String = ""
)

object MemeRepository {
    val templates = listOf(
        MemeTemplate(
            id = "brahmi_shock",
            name = "Brahmanandam Shocked Expression",
            category = "Telugu Classics",
            defaultTopText = "WHEN THE PRODUCTION BUILD SUCCEEDS",
            defaultBottomText = "ON THE VERY FIRST TRY",
            emoji = "😲",
            bgGradientHex = Pair(0xFF7C2D12, 0xFF451A03),
            punchlineTe = "అసలు ఎలా సాధ్యమైంది రా బాబు!",
            description = "Iconic Telugu cinema comedy shock reaction from King of Comedy Brahmanandam."
        ),
        MemeTemplate(
            id = "brahmi_thinking",
            name = "Brahmanandam Deep Calculations",
            category = "Telugu Classics",
            defaultTopText = "ME CALCULATING 10 MINUTE REEL",
            defaultBottomText = "WHILE SLEEPING ONLY 3 HOURS",
            emoji = "🤔",
            bgGradientHex = Pair(0xFF1E3A8A, 0xFF172554),
            punchlineTe = "ఏం కాలిక్యులేషన్స్ రా నాయనా!",
            description = "Hilarious Telugu overthinking and mastermind plotting scene."
        ),
        MemeTemplate(
            id = "ali_katravalli",
            name = "Ali Comedy Laugh",
            category = "Telugu Classics",
            defaultTopText = "CLIENT ASKS FOR CHANGES",
            defaultBottomText = "AFTER VIDEO IS ALREADY EXPORTED",
            emoji = "😂",
            bgGradientHex = Pair(0xFF831843, 0xFF500724),
            punchlineTe = "కత్రవల్లి! ఇంకేమైనా మిగిలిందా?",
            description = "Evergreen Telugu laugh and sarcastic celebration reaction."
        ),
        MemeTemplate(
            id = "drake_hotline",
            name = "Drake Dislike / Like",
            category = "Trending",
            defaultTopText = "BORING MANUAL VIDEO EDITING",
            defaultBottomText = "AI OMNIVIDEO AUTO GENERATION",
            emoji = "👉",
            bgGradientHex = Pair(0xFFEA580C, 0xFF9A3412),
            punchlineTe = "ఇది కదా కావాల్సింది!",
            description = "The immortal two-panel approval / rejection meme format."
        ),
        MemeTemplate(
            id = "distracted_bf",
            name = "Distracted Creator",
            category = "Trending",
            defaultTopText = "MY 10 UNFINISHED PROJECTS",
            defaultBottomText = "NEW VIRAL REEL IDEA AT 2 AM",
            emoji = "👀",
            bgGradientHex = Pair(0xFF4338CA, 0xFF312E81),
            punchlineTe = "కొత్త ఐడియా వస్తే ఆగలేం మరి!",
            description = "Looking at tempting shiny new viral trends."
        ),
        MemeTemplate(
            id = "gigachad",
            name = "Sigma Gigachad",
            category = "Trending",
            defaultTopText = "YES, I EXPORT ALL MY VIDEOS",
            defaultBottomText = "OFFLINE IN 4K WITH OMNIVIDEO",
            emoji = "🗿",
            bgGradientHex = Pair(0xFF374151, 0xFF111827),
            punchlineTe = "ఒరిజినల్ మాస్ ఆటిట్యూడ్!",
            description = "Ultra confidence and undisputed sigma creator energy."
        ),
        MemeTemplate(
            id = "surprised_pikachu",
            name = "Surprised Pikachu",
            category = "Reactions",
            defaultTopText = "POSTS SHORT WITH ZERO HASHTAGS",
            defaultBottomText = "WONDERS WHY IT GETS 4 VIEWS",
            emoji = "⚡",
            bgGradientHex = Pair(0xFFCA8A04, 0xFF854D0E),
            punchlineTe = "అదే కదా మ్యాజిక్!",
            description = "Classic self-inflicted confusion and jaw-drop reaction."
        ),
        MemeTemplate(
            id = "this_is_fine",
            name = "This Is Fine Dog",
            category = "Reactions",
            defaultTopText = "BATTERY AT 1% WHILE RENDERING",
            defaultBottomText = "THIS IS COMPLETELY FINE",
            emoji = "🔥",
            bgGradientHex = Pair(0xFFB91C1C, 0xFF7F1D1D),
            punchlineTe = "అంతా ప్రశాంతంగానే ఉంది!",
            description = "Drinking coffee calmly while chaos burns around."
        ),
        MemeTemplate(
            id = "roll_safe",
            name = "Roll Safe (Big Brain)",
            category = "Gaming & Tech",
            defaultTopText = "YOU CAN'T MISS SOCIAL DEADLINES",
            defaultBottomText = "IF YOU AUTO-SCHEDULE IN ADVANCE",
            emoji = "🧠",
            bgGradientHex = Pair(0xFF047857, 0xFF064E3B),
            punchlineTe = "మెదడు అంటే ఇదీ!",
            description = "Tapping temple pointing to unassailable galaxy-brain logic."
        )
    )
}
