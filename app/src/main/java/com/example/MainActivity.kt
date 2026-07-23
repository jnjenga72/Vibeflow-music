package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.VibeDatabase
import com.example.data.model.UserRole
import com.example.data.repository.MusicRepository
import com.example.ui.components.AuthModal
import com.example.ui.components.BottomPlayerBar
import com.example.ui.components.FullScreenPlayerSheet
import com.example.ui.components.PaywallModal
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.ArtistStudioScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.PremiumScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.PremiumGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VibeFlowTheme
import com.example.ui.theme.VibeGreen
import com.example.ui.viewmodel.VibeViewModel
import com.example.ui.viewmodel.VibeViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: VibeViewModel by viewModels {
        val database = VibeDatabase.getDatabase(applicationContext)
        val repository = MusicRepository(database.dao())
        VibeViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VibeFlowTheme {
                VibeFlowApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun VibeFlowApp(viewModel: VibeViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()

    val songs by viewModel.songs.collectAsStateWithLifecycle()
    val likedSongs by viewModel.likedSongs.collectAsStateWithLifecycle()
    val downloadedSongs by viewModel.downloadedSongs.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val paymentRecords by viewModel.paymentRecords.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    val currentSong by viewModel.currentSong.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val playbackPositionMs by viewModel.playbackPositionMs.collectAsStateWithLifecycle()
    val durationMs by viewModel.durationMs.collectAsStateWithLifecycle()
    val isRepeat by viewModel.isRepeat.collectAsStateWithLifecycle()
    val isShuffle by viewModel.isShuffle.collectAsStateWithLifecycle()
    val playbackSpeed by viewModel.playbackSpeed.collectAsStateWithLifecycle()
    val volume by viewModel.volume.collectAsStateWithLifecycle()

    val showPaywall by viewModel.showPaywall.collectAsStateWithLifecycle()
    val showAuthModal by viewModel.showAuthModal.collectAsStateWithLifecycle()
    val showFullScreenPlayer by viewModel.showFullScreenPlayer.collectAsStateWithLifecycle()
    val showLyricsView by viewModel.showLyricsView.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val selectedPlan by viewModel.selectedPlan.collectAsStateWithLifecycle()
    val mPesaPhoneNumber by viewModel.mPesaPhoneNumber.collectAsStateWithLifecycle()
    val mPesaStatus by viewModel.mPesaStatus.collectAsStateWithLifecycle()
    val mPesaCountDown by viewModel.mPesaCountDown.collectAsStateWithLifecycle()

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBackground)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Bottom Mini Player
                AnimatedVisibility(
                    visible = currentSong != null,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut()
                ) {
                    currentSong?.let { song ->
                        BottomPlayerBar(
                            song = song,
                            isPlaying = isPlaying,
                            playbackPositionMs = playbackPositionMs,
                            durationMs = durationMs,
                            onPlayPauseToggle = { viewModel.togglePlayPause() },
                            onSkipNext = { viewModel.skipToNext() },
                            onLikeToggle = { viewModel.toggleLikeSong(song) },
                            onClick = { viewModel.toggleFullScreenPlayer(true) }
                        )
                    }
                }

                // Bottom Navigation Tabs
                NavigationBar(
                    containerColor = DarkSurface,
                    contentColor = TextPrimary,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = activeTab == "home",
                        onClick = { viewModel.selectTab("home") },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home", modifier = Modifier.size(22.dp)) },
                        label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkBackground,
                            selectedTextColor = VibeGreen,
                            indicatorColor = VibeGreen,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_home")
                    )

                    NavigationBarItem(
                        selected = activeTab == "search",
                        onClick = { viewModel.selectTab("search") },
                        icon = { Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(22.dp)) },
                        label = { Text("Search", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkBackground,
                            selectedTextColor = VibeGreen,
                            indicatorColor = VibeGreen,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_search")
                    )

                    NavigationBarItem(
                        selected = activeTab == "library",
                        onClick = { viewModel.selectTab("library") },
                        icon = { Icon(Icons.Default.LibraryMusic, contentDescription = "Library", modifier = Modifier.size(22.dp)) },
                        label = { Text("Library", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkBackground,
                            selectedTextColor = VibeGreen,
                            indicatorColor = VibeGreen,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_library")
                    )

                    NavigationBarItem(
                        selected = activeTab == "premium",
                        onClick = { viewModel.selectTab("premium") },
                        icon = { Icon(Icons.Default.Star, contentDescription = "Premium", modifier = Modifier.size(22.dp)) },
                        label = { Text("Premium", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkBackground,
                            selectedTextColor = PremiumGold,
                            indicatorColor = PremiumGold,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_premium")
                    )

                    NavigationBarItem(
                        selected = activeTab == "studio",
                        onClick = { viewModel.selectTab("studio") },
                        icon = {
                            Icon(
                                imageVector = if (currentUser.role == UserRole.ADMIN) Icons.Default.AdminPanelSettings else Icons.Default.MusicNote,
                                contentDescription = "Studio",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = if (currentUser.role == UserRole.ADMIN) "Admin" else "Studio",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkBackground,
                            selectedTextColor = VibeGreen,
                            indicatorColor = VibeGreen,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_studio")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            when (activeTab) {
                "home" -> HomeScreen(
                    currentUser = currentUser,
                    songs = songs,
                    featuredAlbums = viewModel.featuredAlbums,
                    currentSong = currentSong,
                    isPlaying = isPlaying,
                    onSongClick = { viewModel.playSong(it) },
                    onLikeClick = { viewModel.toggleLikeSong(it) },
                    onDownloadClick = { viewModel.handleDownloadClick(it) },
                    onOpenAuthModal = { viewModel.toggleAuthModal(true) },
                    onOpenPaywall = { viewModel.openPaywall() }
                )

                "search" -> SearchScreen(
                    searchQuery = searchQuery,
                    searchResults = searchResults,
                    genres = viewModel.genres,
                    currentSong = currentSong,
                    isPlaying = isPlaying,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    onSongClick = { viewModel.playSong(it) },
                    onLikeClick = { viewModel.toggleLikeSong(it) },
                    onDownloadClick = { viewModel.handleDownloadClick(it) }
                )

                "library" -> LibraryScreen(
                    userRole = currentUser.role,
                    playlists = playlists,
                    likedSongs = likedSongs,
                    downloadedSongs = downloadedSongs,
                    currentSong = currentSong,
                    isPlaying = isPlaying,
                    onSongClick = { viewModel.playSong(it) },
                    onLikeClick = { viewModel.toggleLikeSong(it) },
                    onDownloadClick = { viewModel.handleDownloadClick(it) },
                    onCreatePlaylist = { name, desc -> viewModel.createPlaylist(name, desc) },
                    onOpenPaywall = { viewModel.openPaywall() }
                )

                "premium" -> PremiumScreen(
                    userRole = currentUser.role,
                    subscriptionPlans = viewModel.subscriptionPlans,
                    onOpenPaywallModal = { plan -> viewModel.openPaywall(plan) }
                )

                "studio" -> {
                    if (currentUser.role == UserRole.ADMIN) {
                        AdminPanelScreen(currentUser = currentUser)
                    } else {
                        ArtistStudioScreen(
                            currentUser = currentUser,
                            songs = songs,
                            onUploadSong = { title, album, genre, audioUrl, coverUrl ->
                                viewModel.uploadArtistSong(title, album, genre, audioUrl, coverUrl)
                            }
                        )
                    }
                }
            }
        }
    }

    // Modal Overlays
    if (showFullScreenPlayer && currentSong != null) {
        currentSong?.let { song ->
            FullScreenPlayerSheet(
                song = song,
                isPlaying = isPlaying,
                playbackPositionMs = playbackPositionMs,
                durationMs = durationMs,
                isRepeat = isRepeat,
                isShuffle = isShuffle,
                playbackSpeed = playbackSpeed,
                volume = volume,
                showLyricsView = showLyricsView,
                onDismiss = { viewModel.toggleFullScreenPlayer(false) },
                onPlayPauseToggle = { viewModel.togglePlayPause() },
                onSkipNext = { viewModel.skipToNext() },
                onSkipPrevious = { viewModel.skipToPrevious() },
                onSeek = { pos -> viewModel.seekTo(pos) },
                onToggleRepeat = { viewModel.toggleRepeat() },
                onToggleShuffle = { viewModel.toggleShuffle() },
                onToggleLike = { viewModel.toggleLikeSong(song) },
                onDownloadClick = { viewModel.handleDownloadClick(song) },
                onToggleLyrics = { viewModel.toggleLyricsView() },
                onSpeedChange = { spd -> viewModel.setPlaybackSpeed(spd) },
                onVolumeChange = { vol -> viewModel.setVolume(vol) }
            )
        }
    }

    if (showPaywall) {
        PaywallModal(
            plans = viewModel.subscriptionPlans,
            selectedPlan = selectedPlan,
            mPesaPhoneNumber = mPesaPhoneNumber,
            mPesaStatus = mPesaStatus,
            mPesaCountDown = mPesaCountDown,
            paymentRecords = paymentRecords,
            onDismiss = { viewModel.closePaywall() },
            onSelectPlan = { plan -> viewModel.selectPlan(plan) },
            onPhoneChange = { phone -> viewModel.setMpesaPhoneNumber(phone) },
            onInitiateMpesa = { viewModel.initiateMpesaStkPush() }
        )
    }

    if (showAuthModal) {
        AuthModal(
            currentUser = currentUser,
            onDismiss = { viewModel.toggleAuthModal(false) },
            onSelectRole = { role -> viewModel.setUserRole(role) }
        )
    }
}
