package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Playlist
import com.example.data.model.Song
import com.example.data.model.UserRole
import com.example.ui.theme.CardDark
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VibeGreen

@Composable
fun LibraryScreen(
    userRole: UserRole,
    playlists: List<Playlist>,
    likedSongs: List<Song>,
    downloadedSongs: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    onSongClick: (Song) -> Unit,
    onLikeClick: (Song) -> Unit,
    onDownloadClick: (Song) -> Unit,
    onCreatePlaylist: (String, String) -> Unit,
    onOpenPaywall: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("Playlists") } // Playlists, Liked Songs, Downloads
    var showCreateDialog by remember { mutableStateOf(false) }
    var playlistName by remember { mutableStateOf("") }
    var playlistDesc by remember { mutableStateOf("") }

    val filters = listOf("Playlists", "Liked Songs", "Downloads")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
            .testTag("library_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Library",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(CardDark)
                    .clickable { showCreateDialog = true }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("create_playlist_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New",
                        tint = VibeGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "New Playlist",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Chips Row
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filters) { filter ->
                val isSelected = selectedFilter == filter
                AssistChip(
                    onClick = { selectedFilter = filter },
                    label = {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) DarkBackground else TextPrimary
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (isSelected) VibeGreen else CardDark
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedFilter) {
            "Playlists" -> {
                LazyColumn(contentPadding = PaddingValues(bottom = 120.dp)) {
                    // Liked Songs Banner Tile
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CardDark)
                                .clickable { selectedFilter = "Liked Songs" }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(VibeGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Favorite, contentDescription = null, tint = DarkBackground)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Liked Songs", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("${likedSongs.size} saved songs", fontSize = 12.sp, color = TextSecondary)
                            }
                        }
                    }

                    items(playlists) { pl ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceVariant)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = pl.coverUrl,
                                contentDescription = pl.name,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(pl.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(pl.description, fontSize = 12.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }

            "Liked Songs" -> {
                if (likedSongs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(bottom = 120.dp), contentAlignment = Alignment.Center) {
                        Text("No liked songs yet. Tap ♡ on any song!", color = TextMuted)
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(bottom = 120.dp)) {
                        items(likedSongs) { song ->
                            SongListItem(
                                song = song,
                                isPlaying = isPlaying && currentSong?.id == song.id,
                                onSongClick = { onSongClick(song) },
                                onLikeClick = { onLikeClick(song) },
                                onDownloadClick = { onDownloadClick(song) }
                            )
                        }
                    }
                }
            }

            "Downloads" -> {
                if (userRole == UserRole.FREE || userRole == UserRole.GUEST) {
                    // Paywall callout inside Downloads
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardDark),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = VibeGreen, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Offline Downloads Restricted", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Free users cannot download or play music offline. Upgrade to VibeFlow Premium for unlimited encrypted downloads!", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(horizontal = 8.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onOpenPaywall,
                                colors = ButtonDefaults.buttonColors(containerColor = VibeGreen, contentColor = DarkBackground),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Unlock Offline Downloads", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    if (downloadedSongs.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize().padding(bottom = 120.dp), contentAlignment = Alignment.Center) {
                            Text("No downloaded songs yet. Tap ⬇ on any song!", color = TextMuted)
                        }
                    } else {
                        LazyColumn(contentPadding = PaddingValues(bottom = 120.dp)) {
                            items(downloadedSongs) { song ->
                                SongListItem(
                                    song = song,
                                    isPlaying = isPlaying && currentSong?.id == song.id,
                                    onSongClick = { onSongClick(song) },
                                    onLikeClick = { onLikeClick(song) },
                                    onDownloadClick = { onDownloadClick(song) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Create Playlist Dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create Playlist", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = playlistName,
                        onValueChange = { playlistName = it },
                        label = { Text("Playlist Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VibeGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.2f), focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = playlistDesc,
                        onValueChange = { playlistDesc = it },
                        label = { Text("Description (Optional)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VibeGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.2f), focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (playlistName.isNotBlank()) {
                            onCreatePlaylist(playlistName, playlistDesc)
                            showCreateDialog = false
                            playlistName = ""
                            playlistDesc = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VibeGreen, contentColor = DarkBackground)
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkBackground
        )
    }
}
