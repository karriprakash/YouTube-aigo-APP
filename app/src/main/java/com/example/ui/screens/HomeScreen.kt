package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.VideoCameraBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.VideoLibrary
import com.example.data.model.MediaAsset
import com.example.data.model.MemeRepository
import com.example.data.model.UserVideoMetadata
import com.example.data.model.VideoProject
import com.example.ui.components.AssetLibrarySection
import com.example.ui.components.AudioToVideoDialog
import com.example.ui.components.CreateProjectDialog
import com.example.ui.components.HomeScreenMemeHub
import com.example.ui.components.MemeStudioSection
import com.example.ui.components.OfflineVideosSection
import com.example.ui.components.ProjectsListSection
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.SunsetAmber
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    user: com.google.firebase.auth.FirebaseUser?,
    onRequestSignIn: () -> Unit = {},
    onSignOut: () -> Unit,
    onOpenProject: (VideoProject) -> Unit,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.projects.collectAsState()
    val assets by viewModel.assets.collectAsState()
    val userVideos by viewModel.userVideos.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var selectedHomeTab by remember { mutableIntStateOf(0) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showAudioToVideoDialog by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(NeonIndigo, Color(0xFFA855F7))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Movie,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "OmniVideo",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Spacer(Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF312E81)
                                ) {
                                    Text(
                                        text = "AI STUDIO",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Telugu & Multi-Platform Video Generator",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = if (user == null) SunsetAmber else NeonIndigo
                        ),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { showAccountDialog = true }
                            .testTag("account_chip_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            if (user != null) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(NeonIndigo),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user.email?.take(1)?.uppercase() ?: "U",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Account",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = SunsetAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    text = "Try Mode",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "• Sign In",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NeonCyan
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ExtendedFloatingActionButton(
                    onClick = { showAudioToVideoDialog = true },
                    containerColor = Color(0xFF1E1B4B),
                    contentColor = NeonCyan,
                    icon = { Icon(Icons.Default.GraphicEq, contentDescription = null, tint = NeonCyan) },
                    text = { Text("Audio to Video", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("audio_to_video_fab")
                )
                ExtendedFloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = NeonIndigo,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("New Video", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("new_video_fab")
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedHomeTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = NeonIndigo,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedHomeTab]),
                        color = NeonIndigo,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedHomeTab == 0,
                    onClick = { selectedHomeTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Projects (${projects.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    },
                    modifier = Modifier.testTag("tab_projects")
                )
                Tab(
                    selected = selectedHomeTab == 1,
                    onClick = { selectedHomeTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Saved Videos (${userVideos.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    },
                    modifier = Modifier.testTag("tab_saved_videos")
                )
                Tab(
                    selected = selectedHomeTab == 2,
                    onClick = { selectedHomeTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEmotions, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Memes (${MemeRepository.templates.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    },
                    modifier = Modifier.testTag("tab_memes")
                )
                Tab(
                    selected = selectedHomeTab == 3,
                    onClick = { selectedHomeTab = 3 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Assets (${assets.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    },
                    modifier = Modifier.testTag("tab_assets")
                )
            }

            Spacer(Modifier.height(8.dp))

            // Body
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (selectedHomeTab == 0) {
                    if (user == null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .testTag("try_mode_banner"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131E31)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2563EB))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(SunsetAmber.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayCircle,
                                        contentDescription = null,
                                        tint = SunsetAmber,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Studio Try Mode (Offline Access)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "All tools unlocked. Trim, audio mix, Telugu AI summaries & export freely! Sign in with Google anytime to back up.",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Button(
                                    onClick = onRequestSignIn,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = Color(0xFF0F172A)
                                    ),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("try_banner_signin_btn")
                                ) {
                                    Text("Sign In", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Feature Banner
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2433)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C324B))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(SunsetAmber.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = SunsetAmber,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Multilingual Video Studio",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Raw Telugu / English audio into YouTube (16:9), Instagram (9:16), and TikTok with offline persistence.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }

                    // Quick Action: Audio & Transcription Video Studio Banner
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("audio_studio_banner"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161533)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonIndigo)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(NeonIndigo.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Audio & Transcription Studio",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Turn voice or audio into video with live transcription & Google Photos backgrounds.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = { showAudioToVideoDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonIndigo),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("start_audio_video_btn")
                            ) {
                                Text("Create", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    ProjectsListSection(
                        projects = projects,
                        onSelectProject = onOpenProject,
                        onDeleteProject = { viewModel.deleteProject(it) }
                    )
                } else if (selectedHomeTab == 1) {
                    OfflineVideosSection(
                        userVideos = userVideos,
                        onDeleteVideo = { viewModel.deleteUserVideo(it) }
                    )
                } else if (selectedHomeTab == 2) {
                    HomeScreenMemeHub(
                        onCreateProjectWithMeme = { meme ->
                            viewModel.createBlankProject(
                                title = "${meme.name} Reel",
                                durationSec = 15,
                                language = "te",
                                notes = "${meme.defaultTopText} - ${meme.defaultBottomText}"
                            )
                            viewModel.updateProjectMeme(
                                memeId = meme.id,
                                title = meme.name,
                                topText = meme.defaultTopText,
                                bottomText = meme.defaultBottomText
                            )
                            viewModel.activeProject.value?.let { onOpenProject(it) }
                        }
                    )
                } else {
                    AssetLibrarySection(
                        assets = assets,
                        onCreateProjectFromAsset = { asset ->
                            viewModel.createProjectFromAsset(asset)
                            viewModel.activeProject.value?.let { onOpenProject(it) }
                        },
                        onAddCustomAsset = { name, type, uri, dur, lang, desc ->
                            viewModel.addCustomMediaAsset(name, type, uri, dur, lang, desc)
                        }
                    )
                }

                Spacer(Modifier.height(80.dp))
            }
        }
    }

    if (showCreateDialog) {
        CreateProjectDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { title, duration, lang, notes ->
                viewModel.createBlankProject(title, duration, lang, notes)
                viewModel.activeProject.value?.let { onOpenProject(it) }
            }
        )
    }

    if (showAudioToVideoDialog) {
        AudioToVideoDialog(
            onDismiss = { showAudioToVideoDialog = false },
            onCreateAudioVideoProject = { title, durationMs, lang, audioUri, transcript, segs, aspect, bgUri, bgType, bgSrc ->
                viewModel.createProjectFromAudioSource(
                    title, durationMs, lang, audioUri, transcript, segs, aspect, bgUri, bgType, bgSrc
                )
                viewModel.activeProject.value?.let { onOpenProject(it) }
            }
        )
    }

    if (showAccountDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showAccountDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (user != null) NeonIndigo else SunsetAmber),
                        contentAlignment = Alignment.Center
                    ) {
                        if (user != null) {
                            Text(
                                text = user.email?.take(1)?.uppercase() ?: "U",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        } else {
                            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = if (user != null) "Account Profile" else "Studio Try Mode",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column {
                    if (user != null) {
                        Text(
                            text = "Signed in with Google:",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = user.email ?: "Unknown User",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E2433),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Cloud Status: Synced", fontSize = 11.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                Text("Your projects and drafts are backed up with Firebase zero-trust security.", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            }
                        }
                    } else {
                        Text(
                            text = "Currently Using OmniVideo in Try Mode",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "You have full access to video trimming, text overlays, audio levels, color filters, Telugu AI summaries, and social exports saved locally on this device.",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E2433),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Local Storage: Active (Room Database)", fontSize = 11.sp, color = SunsetAmber, fontWeight = FontWeight.Bold)
                                Text("Sign in with Google anytime to back up projects to cloud and sync across devices.", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (user != null) {
                    androidx.compose.material3.Button(
                        onClick = {
                            showAccountDialog = false
                            onSignOut()
                        },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444)
                        )
                    ) {
                        Text("Sign Out", fontWeight = FontWeight.Bold)
                    }
                } else {
                    androidx.compose.material3.Button(
                        onClick = {
                            showAccountDialog = false
                            onRequestSignIn()
                        },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF0F172A)
                        ),
                        modifier = Modifier.testTag("dialog_signin_btn")
                    ) {
                        Text("Sign in with Google", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showAccountDialog = false }) {
                    Text(if (user != null) "Close" else "Keep Trying Offline")
                }
            }
        )
    }
}
