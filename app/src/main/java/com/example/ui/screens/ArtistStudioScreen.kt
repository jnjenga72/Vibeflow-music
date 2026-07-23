package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Song
import com.example.data.model.User
import com.example.ui.theme.CardDark
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.MpesaGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VibeGreen

@Composable
fun ArtistStudioScreen(
    currentUser: User,
    songs: List<Song>,
    onUploadSong: (String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var album by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("Afrobeats") }
    var audioUrl by remember { mutableStateOf("https://cdn.soundhelix.com/audio-samples/SoundHelix-Song-1.mp3") }
    var coverUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=500") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
            .testTag("artist_studio_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text("Artist Creator Studio", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("Publish music, track streams & manage earnings", fontSize = 12.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(16.dp))

            // Artist Analytics Dashboard Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticsCard("Streams", "1.4M", Icons.Default.Equalizer, VibeGreen, Modifier.weight(1f))
                AnalyticsCard("Listeners", "85.2k", Icons.Default.People, Color(0xFF3B82F6), Modifier.weight(1f))
                AnalyticsCard("Earnings", "Ksh 142.5k", Icons.Default.Payments, MpesaGreen, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Song Uploader Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = VibeGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload New Track", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Track Title") },
                        modifier = Modifier.fillMaxWidth().testTag("upload_title_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VibeGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.2f), focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = album,
                        onValueChange = { album = it },
                        label = { Text("Album / EP Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VibeGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.2f), focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = genre,
                        onValueChange = { genre = it },
                        label = { Text("Genre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VibeGreen, unfocusedBorderColor = Color.White.copy(alpha = 0.2f), focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onUploadSong(title, album, genre, audioUrl, coverUrl)
                                title = ""
                                album = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("publish_song_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = VibeGreen, contentColor = DarkBackground),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Publish Track to VibeFlow", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Your Published Songs", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(songs) { song ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurfaceVariant)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(song.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("${song.album} • ${song.genre}", fontSize = 11.sp, color = TextSecondary)
                }
                Text("${song.playCount} streams", fontSize = 12.sp, color = VibeGreen, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AnalyticsCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
            Text(label, fontSize = 11.sp, color = TextSecondary)
        }
    }
}
