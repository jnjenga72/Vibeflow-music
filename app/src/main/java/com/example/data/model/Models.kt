package com.example.data.model

enum class UserRole {
    GUEST,
    FREE,
    PREMIUM,
    ARTIST,
    ADMIN
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole = UserRole.FREE,
    val avatarUrl: String = "",
    val isVerified: Boolean = false,
    val subscriptionPlan: String? = null
)

data class LyricLine(
    val timestampMs: Long,
    val text: String
)

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationSec: Int,
    val audioUrl: String,
    val coverUrl: String,
    val lyrics: List<LyricLine> = emptyList(),
    val genre: String = "Pop",
    val isLiked: Boolean = false,
    val isDownloaded: Boolean = false,
    val playCount: Long = 0,
    val releaseDate: String = "2026"
)

data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val coverUrl: String,
    val releaseYear: String,
    val songsCount: Int
)

data class Playlist(
    val id: String,
    val name: String,
    val description: String,
    val coverUrl: String,
    val isPublic: Boolean = true,
    val isCollaborative: Boolean = false,
    val songCount: Int = 0,
    val createdBy: String = "User"
)

data class SubscriptionPlan(
    val id: String,
    val name: String,
    val priceKsh: Int,
    val billingCycle: String,
    val isPopular: Boolean = false,
    val features: List<String>
)

data class PaymentRecord(
    val id: String,
    val planName: String,
    val amountKsh: Int,
    val paymentMethod: String,
    val transactionRef: String,
    val timestamp: String,
    val status: String = "COMPLETED"
)

data class Genre(
    val id: String,
    val name: String,
    val hexGradientStart: Long,
    val hexGradientEnd: Long,
    val iconName: String = "music"
)

data class ArtistStats(
    val totalStreams: Long,
    val monthlyListeners: Long,
    val totalEarningsKsh: Int,
    val followers: Int
)
