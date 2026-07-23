package com.example.data.repository

import com.example.data.local.PaymentEntity
import com.example.data.local.PlaylistEntity
import com.example.data.local.SongEntity
import com.example.data.local.VibeDao
import com.example.data.model.Album
import com.example.data.model.Genre
import com.example.data.model.LyricLine
import com.example.data.model.PaymentRecord
import com.example.data.model.Playlist
import com.example.data.model.Song
import com.example.data.model.SubscriptionPlan
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MusicRepository(private val dao: VibeDao) {

    val allSongs: Flow<List<Song>> = dao.getAllSongs().map { entities ->
        if (entities.isEmpty()) getSeedSongs() else entities.map { it.toSong() }
    }

    val likedSongs: Flow<List<Song>> = dao.getLikedSongs().map { entities ->
        entities.map { it.toSong() }
    }

    val downloadedSongs: Flow<List<Song>> = dao.getDownloadedSongs().map { entities ->
        entities.map { it.toSong() }
    }

    val userPlaylists: Flow<List<Playlist>> = dao.getAllPlaylists().map { entities ->
        entities.map { Playlist(it.id, it.name, it.description, it.coverUrl, it.isPublic, false, it.songCount) }
    }

    val paymentRecords: Flow<List<PaymentRecord>> = dao.getAllPayments().map { entities ->
        entities.map { PaymentRecord(it.id, it.planName, it.amountKsh, it.method, it.transactionRef, it.timestamp, it.status) }
    }

    suspend fun seedDatabaseIfEmpty() {
        // Initial seed data
        val initialPlaylists = listOf(
            PlaylistEntity("pl_1", "Chill Afrobeats", "Smooth rhythms for relaxing nights", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500", true, 8),
            PlaylistEntity("pl_2", "Workout Beast Mode", "High energy bangers to smash your goals", "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=500", true, 12),
            PlaylistEntity("pl_3", "Nairobi Nightlife Vibe", "Top trending East African club hits", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500", true, 15),
            PlaylistEntity("pl_4", "Deep Focus Lo-Fi", "Instrumental beats for studying and coding", "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=500", true, 20)
        )
        for (pl in initialPlaylists) {
            dao.insertPlaylist(pl)
        }

        val seeds = getSeedSongs()
        dao.insertSongs(seeds.map { it.toEntity() })
    }

    suspend fun toggleLike(songId: String, currentStatus: Boolean) {
        dao.updateLiked(songId, !currentStatus)
    }

    suspend fun toggleDownload(songId: String, currentStatus: Boolean): Boolean {
        dao.updateDownloaded(songId, !currentStatus)
        return !currentStatus
    }

    suspend fun createPlaylist(name: String, description: String) {
        val newId = "pl_${System.currentTimeMillis()}"
        val entity = PlaylistEntity(
            id = newId,
            name = name,
            description = description.ifEmpty { "Custom VibeFlow playlist" },
            coverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500",
            isPublic = true,
            songCount = 0
        )
        dao.insertPlaylist(entity)
    }

    suspend fun addArtistSong(title: String, artist: String, album: String, genre: String, audioUrl: String, coverUrl: String) {
        val newId = "song_${System.currentTimeMillis()}"
        val newSong = SongEntity(
            id = newId,
            title = title,
            artist = artist.ifEmpty { "Vibe Artist" },
            album = album.ifEmpty { "Single" },
            durationSec = 215,
            audioUrl = audioUrl.ifEmpty { "https://cdn.soundhelix.com/audio-samples/SoundHelix-Song-1.mp3" },
            coverUrl = coverUrl.ifEmpty { "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=500" },
            genre = genre,
            isLiked = false,
            isDownloaded = false,
            playCount = 100
        )
        dao.insertSong(newSong)
    }

    suspend fun recordMpesaPayment(planName: String, priceKsh: Int, phoneNum: String, refCode: String) {
        val payment = PaymentEntity(
            id = "pay_${System.currentTimeMillis()}",
            planName = planName,
            amountKsh = priceKsh,
            method = "M-Pesa STK ($phoneNum)",
            transactionRef = refCode,
            timestamp = "Today, " + java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date()),
            status = "SUCCESS"
        )
        dao.insertPayment(payment)
    }

    fun getGenres(): List<Genre> = listOf(
        Genre("g1", "Afrobeats", 0xFF10B981, 0xFF059669),
        Genre("g2", "Amapiano", 0xFF8B5CF6, 0xFF6D28D9),
        Genre("g3", "Hip-Hop", 0xFFEF4444, 0xFFB91C1C),
        Genre("g4", "R&B / Soul", 0xFFEC4899, 0xFFBE185D),
        Genre("g5", "Reggae & Dancehall", 0xFFF59E0B, 0xFFD97706),
        Genre("g6", "Electronic / EDM", 0xFF3B82F6, 0xFF1D4ED8),
        Genre("g7", "Lo-Fi Focus", 0xFF6366F1, 0xFF4338CA),
        Genre("g8", "Pop Gold", 0xFF10B981, 0xFF047857)
    )

    fun getSubscriptionPlans(): List<SubscriptionPlan> = listOf(
        SubscriptionPlan(
            id = "plan_1m",
            name = "1 Month Premium",
            priceKsh = 199,
            billingCycle = "Billed monthly",
            isPopular = false,
            features = listOf("Unlimited streaming", "No advertisements", "High quality audio (320kbps)", "Unlimited skips", "Offline music downloads")
        ),
        SubscriptionPlan(
            id = "plan_3m",
            name = "3 Months Premium",
            priceKsh = 499,
            billingCycle = "Save Ksh 98",
            isPopular = true,
            features = listOf("Everything in 1 Month", "10% Discount", "Early access releases", "Cross-device sync", "Offline encrypted playback")
        ),
        SubscriptionPlan(
            id = "plan_6m",
            name = "6 Months Premium",
            priceKsh = 899,
            billingCycle = "Save Ksh 295",
            isPopular = false,
            features = listOf("Everything in 3 Months", "Best value solo plan", "Smart AI recommendations", "Priority support")
        ),
        SubscriptionPlan(
            id = "plan_12m",
            name = "12 Months Premium",
            priceKsh = 1599,
            billingCycle = "Save Ksh 789 per year",
            isPopular = false,
            features = listOf("Full annual access", "Huge 33% savings", "VIP Badge profile", "Exclusive concert perks")
        ),
        SubscriptionPlan(
            id = "plan_fam",
            name = "Family Plan",
            priceKsh = 2499,
            billingCycle = "1 Year for up to 6 accounts",
            isPopular = false,
            features = listOf("6 Premium accounts", "Shared family mix", "Explicit content block", "Individual playlists & downloads")
        ),
        SubscriptionPlan(
            id = "plan_std",
            name = "Student Plan",
            priceKsh = 99,
            billingCycle = "50% off monthly with student ID",
            isPopular = false,
            features = listOf("Full Premium features", "Special student discount", "Verified annual renewal")
        )
    )

    fun getFeaturedAlbums(): List<Album> = listOf(
        Album("alb_1", "African Giant Waves", "Burna Boy", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=500", "2025", 14),
        Album("alb_2", "Nairobi Midnight", "Bensoul", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500", "2026", 10),
        Album("alb_3", "Timeless Echoes", "Davido", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500", "2024", 12),
        Album("alb_4", "Cyberpunk Symphony", "Vibe Collective", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=500", "2026", 8)
    )

    companion object {
        fun getSeedSongs(): List<Song> = listOf(
            Song(
                id = "s1",
                title = "Nairobi Sunset",
                artist = "Sauti Sol",
                album = "East African Vibe",
                durationSec = 210,
                audioUrl = "https://cdn.soundhelix.com/audio-samples/SoundHelix-Song-1.mp3",
                coverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=500",
                genre = "Afrobeats",
                isLiked = true,
                isDownloaded = false,
                playCount = 245000,
                lyrics = listOf(
                    LyricLine(0, "Under the golden sky of Nairobi..."),
                    LyricLine(5000, "City lights turn on, we feel free..."),
                    LyricLine(12000, "Dancing to the rhythm of the night..."),
                    LyricLine(18000, "Everything is gonna be alright..."),
                    LyricLine(25000, "Feel the vibe in your soul..."),
                    LyricLine(32000, "Let the music take control!")
                )
            ),
            Song(
                id = "s2",
                title = "Amapiano Pulse",
                artist = "Kabza De Small & DJ Maphorisa",
                album = "Piano Groove Vol 3",
                durationSec = 280,
                audioUrl = "https://cdn.soundhelix.com/audio-samples/SoundHelix-Song-2.mp3",
                coverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500",
                genre = "Amapiano",
                isLiked = true,
                isDownloaded = true,
                playCount = 890000,
                lyrics = listOf(
                    LyricLine(0, "[Log drum bass drops]"),
                    LyricLine(8000, "Sip the vibe, move your feet"),
                    LyricLine(15000, "Feel the heavy piano beat!"),
                    LyricLine(24000, "Johannesburg to Nairobi crew"),
                    LyricLine(35000, "This rhythm was made for you!")
                )
            ),
            Song(
                id = "s3",
                title = "Midnight Drive",
                artist = "The Weeknd",
                album = "After Hours Vibe",
                durationSec = 225,
                audioUrl = "https://cdn.soundhelix.com/audio-samples/SoundHelix-Song-3.mp3",
                coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500",
                genre = "R&B / Soul",
                isLiked = false,
                isDownloaded = false,
                playCount = 1200000,
                lyrics = listOf(
                    LyricLine(0, "Driving down the empty highway..."),
                    LyricLine(6000, "Neon signs bleeding in the rain..."),
                    LyricLine(14000, "I see your face when I close my eyes..."),
                    LyricLine(22000, "Hoping we can start again...")
                )
            ),
            Song(
                id = "s4",
                title = "Cyberpunk Echoes",
                artist = "Vibe Flow AI",
                album = "Future Synth 2026",
                durationSec = 198,
                audioUrl = "https://cdn.soundhelix.com/audio-samples/SoundHelix-Song-4.mp3",
                coverUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=500",
                genre = "Electronic / EDM",
                isLiked = true,
                isDownloaded = true,
                playCount = 450000,
                lyrics = listOf(
                    LyricLine(0, "Synthesizer swelling in the dark..."),
                    LyricLine(7000, "Digital dreams ignite the spark..."),
                    LyricLine(16000, "Binary heartbeat, zero and one..."),
                    LyricLine(25000, "We dance until the rising sun!")
                )
            ),
            Song(
                id = "s5",
                title = "Coastline Acoustic",
                artist = "Bensoul ft. Nviiri",
                album = "Mombasa Breeze",
                durationSec = 240,
                audioUrl = "https://cdn.soundhelix.com/audio-samples/SoundHelix-Song-5.mp3",
                coverUrl = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=500",
                genre = "Afrobeats",
                isLiked = false,
                isDownloaded = false,
                playCount = 310000,
                lyrics = listOf(
                    LyricLine(0, "Acoustic strings on the ocean shore..."),
                    LyricLine(8000, "Waves crashing, wanting more..."),
                    LyricLine(16000, "Sweet serenity in the air..."),
                    LyricLine(24000, "No worries, no stress, no care.")
                )
            ),
            Song(
                id = "s6",
                title = "Lo-Fi Study Chill",
                artist = "ChillHop Cafe",
                album = "Midnight Coffee",
                durationSec = 165,
                audioUrl = "https://cdn.soundhelix.com/audio-samples/SoundHelix-Song-6.mp3",
                coverUrl = "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=500",
                genre = "Lo-Fi Focus",
                isLiked = true,
                isDownloaded = false,
                playCount = 670000,
                lyrics = listOf(
                    LyricLine(0, "[Soft vinyl crackle & warm piano chords]"),
                    LyricLine(12000, "Focus your mind, relax your soul"),
                    LyricLine(24000, "Lo-fi beats make you whole")
                )
            )
        )
    }
}

fun SongEntity.toSong(): Song = Song(
    id = id,
    title = title,
    artist = artist,
    album = album,
    durationSec = durationSec,
    audioUrl = audioUrl,
    coverUrl = coverUrl,
    genre = genre,
    isLiked = isLiked,
    isDownloaded = isDownloaded,
    playCount = playCount
)

fun Song.toEntity(): SongEntity = SongEntity(
    id = id,
    title = title,
    artist = artist,
    album = album,
    durationSec = durationSec,
    audioUrl = audioUrl,
    coverUrl = coverUrl,
    genre = genre,
    isLiked = isLiked,
    isDownloaded = isDownloaded,
    playCount = playCount
)
