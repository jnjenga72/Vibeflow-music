package com.example.ui.viewmodel

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.VibeDatabase
import com.example.data.model.Album
import com.example.data.model.Genre
import com.example.data.model.PaymentRecord
import com.example.data.model.Playlist
import com.example.data.model.Song
import com.example.data.model.SubscriptionPlan
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.repository.MusicRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VibeViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private var mPesaJob: Job? = null

    // User & Role State
    private val _currentUser = MutableStateFlow(
        User("usr_1", "Alex Rivers", "alex@vibeflow.com", UserRole.FREE, "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500", true, null)
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // Navigation & Modals
    private val _activeTab = MutableStateFlow("home") // home, search, library, premium, artist, admin
    val activeTab: StateFlow<String> = _activeTab.asStateFlow()

    private val _showPaywall = MutableStateFlow(false)
    val showPaywall: StateFlow<Boolean> = _showPaywall.asStateFlow()

    private val _showAuthModal = MutableStateFlow(false)
    val showAuthModal: StateFlow<Boolean> = _showAuthModal.asStateFlow()

    private val _showFullScreenPlayer = MutableStateFlow(false)
    val showFullScreenPlayer: StateFlow<Boolean> = _showFullScreenPlayer.asStateFlow()

    private val _showLyricsView = MutableStateFlow(false)
    val showLyricsView: StateFlow<Boolean> = _showLyricsView.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Music Repository Data
    val songs: StateFlow<List<Song>> = repository.allSongs.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val likedSongs: StateFlow<List<Song>> = repository.likedSongs.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val downloadedSongs: StateFlow<List<Song>> = repository.downloadedSongs.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val playlists: StateFlow<List<Playlist>> = repository.userPlaylists.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val paymentRecords: StateFlow<List<PaymentRecord>> = repository.paymentRecords.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val genres: List<Genre> = repository.getGenres()
    val subscriptionPlans: List<SubscriptionPlan> = repository.getSubscriptionPlans()
    val featuredAlbums: List<Album> = repository.getFeaturedAlbums()

    // Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<Song>> = combine(songs, searchQuery) { songList, query ->
        if (query.isBlank()) songList else {
            songList.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.artist.contains(query, ignoreCase = true) ||
                        it.album.contains(query, ignoreCase = true) ||
                        it.genre.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Player State
    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _playbackPositionMs = MutableStateFlow(0L)
    val playbackPositionMs: StateFlow<Long> = _playbackPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(1000L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _isRepeat = MutableStateFlow(false)
    val isRepeat: StateFlow<Boolean> = _isRepeat.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _volume = MutableStateFlow(0.85f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    // Payment & STK Push State
    private val _selectedPlan = MutableStateFlow<SubscriptionPlan?>(repository.getSubscriptionPlans().firstOrNull())
    val selectedPlan: StateFlow<SubscriptionPlan?> = _selectedPlan.asStateFlow()

    private val _mPesaPhoneNumber = MutableStateFlow("254712345678")
    val mPesaPhoneNumber: StateFlow<String> = _mPesaPhoneNumber.asStateFlow()

    private val _mPesaStatus = MutableStateFlow("IDLE") // IDLE, PROCESSING, SUCCESS, FAILED
    val mPesaStatus: StateFlow<String> = _mPesaStatus.asStateFlow()

    private val _mPesaCountDown = MutableStateFlow(10)
    val mPesaCountDown: StateFlow<Int> = _mPesaCountDown.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty()
        }
    }

    fun selectTab(tab: String) {
        _activeTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openPaywall(plan: SubscriptionPlan? = null) {
        if (plan != null) {
            _selectedPlan.value = plan
        }
        _showPaywall.value = true
    }

    fun closePaywall() {
        _showPaywall.value = false
        _mPesaStatus.value = "IDLE"
    }

    fun toggleAuthModal(show: Boolean = !_showAuthModal.value) {
        _showAuthModal.value = show
    }

    fun toggleFullScreenPlayer(show: Boolean = !_showFullScreenPlayer.value) {
        _showFullScreenPlayer.value = show
    }

    fun toggleLyricsView() {
        _showLyricsView.value = !_showLyricsView.value
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun setUserRole(role: UserRole) {
        val current = _currentUser.value
        _currentUser.value = current.copy(
            role = role,
            subscriptionPlan = if (role == UserRole.PREMIUM) "1 Month Premium" else null
        )
        showToast("Switched active role to ${role.name}")
    }

    // Music Player Controls
    fun playSong(song: Song) {
        _currentSong.value = song
        _durationMs.value = (song.durationSec * 1000).toLong()
        _playbackPositionMs.value = 0L
        _isPlaying.value = true
        startPlaybackTimer()

        // Attempt real audio streaming playback gracefully
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(song.audioUrl)
                prepareAsync()
                setOnPreparedListener { mp ->
                    mp.start()
                    _durationMs.value = mp.duration.toLong()
                }
                setOnCompletionListener {
                    skipToNext()
                }
            }
        } catch (e: Exception) {
            // Fallback to simulated audio timer
        }
    }

    fun togglePlayPause() {
        val current = _currentSong.value ?: return
        if (_isPlaying.value) {
            _isPlaying.value = false
            try { mediaPlayer?.pause() } catch (_: Exception) {}
        } else {
            _isPlaying.value = true
            try { mediaPlayer?.start() } catch (_: Exception) {}
            startPlaybackTimer()
        }
    }

    fun skipToNext() {
        val currentList = songs.value
        if (currentList.isEmpty()) return
        val current = _currentSong.value
        val currentIndex = currentList.indexOfFirst { it.id == current?.id }
        val nextIndex = if (_isShuffle.value) {
            (0 until currentList.size).random()
        } else {
            (currentIndex + 1) % currentList.size
        }
        playSong(currentList[nextIndex])
    }

    fun skipToPrevious() {
        val currentList = songs.value
        if (currentList.isEmpty()) return
        val current = _currentSong.value
        val currentIndex = currentList.indexOfFirst { it.id == current?.id }
        val prevIndex = if (currentIndex <= 0) currentList.size - 1 else currentIndex - 1
        playSong(currentList[prevIndex])
    }

    fun seekTo(positionMs: Long) {
        _playbackPositionMs.value = positionMs
        try {
            mediaPlayer?.seekTo(positionMs.toInt())
        } catch (_: Exception) {}
    }

    fun toggleRepeat() {
        _isRepeat.value = !_isRepeat.value
        showToast(if (_isRepeat.value) "Repeat ON" else "Repeat OFF")
    }

    fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
        showToast(if (_isShuffle.value) "Shuffle ON" else "Shuffle OFF")
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                mediaPlayer?.playbackParams = mediaPlayer?.playbackParams?.setSpeed(speed) ?: return
            }
        } catch (_: Exception) {}
    }

    fun setVolume(vol: Float) {
        _volume.value = vol
        try {
            mediaPlayer?.setVolume(vol, vol)
        } catch (_: Exception) {}
    }

    fun toggleLikeSong(song: Song) {
        viewModelScope.launch {
            repository.toggleLike(song.id, song.isLiked)
            val updatedStatus = !song.isLiked
            if (_currentSong.value?.id == song.id) {
                _currentSong.value = _currentSong.value?.copy(isLiked = updatedStatus)
            }
            showToast(if (updatedStatus) "Added to Liked Songs" else "Removed from Liked Songs")
        }
    }

    fun handleDownloadClick(song: Song) {
        val user = _currentUser.value
        if (user.role == UserRole.FREE || user.role == UserRole.GUEST) {
            // Download restriction: Free users see paywall
            openPaywall()
            showToast("Upgrade to Premium to download songs for offline playback!")
        } else {
            // Premium user can download
            viewModelScope.launch {
                val newStatus = repository.toggleDownload(song.id, song.isDownloaded)
                if (_currentSong.value?.id == song.id) {
                    _currentSong.value = _currentSong.value?.copy(isDownloaded = newStatus)
                }
                showToast(if (newStatus) "Downloaded for offline playback!" else "Removed from offline downloads")
            }
        }
    }

    fun createPlaylist(name: String, description: String) {
        viewModelScope.launch {
            repository.createPlaylist(name, description)
            showToast("Playlist '$name' created!")
        }
    }

    fun uploadArtistSong(title: String, album: String, genre: String, audioUrl: String, coverUrl: String) {
        viewModelScope.launch {
            repository.addArtistSong(title, _currentUser.value.name, album, genre, audioUrl, coverUrl)
            showToast("Song '$title' published successfully!")
        }
    }

    // M-Pesa STK Push Integration Simulation
    fun setMpesaPhoneNumber(num: String) {
        _mPesaPhoneNumber.value = num
    }

    fun selectPlan(plan: SubscriptionPlan) {
        _selectedPlan.value = plan
    }

    fun initiateMpesaStkPush() {
        val plan = _selectedPlan.value ?: return
        val phone = _mPesaPhoneNumber.value
        if (phone.length < 9) {
            showToast("Please enter a valid M-Pesa phone number")
            return
        }

        _mPesaStatus.value = "PROCESSING"
        _mPesaCountDown.value = 8

        mPesaJob?.cancel()
        mPesaJob = viewModelScope.launch {
            for (i in 8 downTo 1) {
                _mPesaCountDown.value = i
                delay(1000)
            }
            // Payment success callback handled
            val refCode = "WSK" + (100000..999999).random()
            repository.recordMpesaPayment(plan.name, plan.priceKsh, phone, refCode)

            _mPesaStatus.value = "SUCCESS"
            // Activate Premium role immediately
            setUserRole(UserRole.PREMIUM)
            showToast("M-Pesa Payment Received! Premium Activated.")
        }
    }

    private fun startPlaybackTimer() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (_isPlaying.value) {
                delay(1000)
                if (_isPlaying.value) {
                    var newPos = _playbackPositionMs.value + (1000 * _playbackSpeed.value).toLong()
                    if (newPos >= _durationMs.value) {
                        if (_isRepeat.value) {
                            newPos = 0L
                        } else {
                            skipToNext()
                            break
                        }
                    }
                    _playbackPositionMs.value = newPos
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
    }
}

class VibeViewModelFactory(private val repository: MusicRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VibeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VibeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
