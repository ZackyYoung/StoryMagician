package com.bytedance.storymagician.components

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.bytedance.storymagician.viewmodel.SharedViewModel
import androidx.core.net.toUri

@Composable
fun PreviewScreen(viewModel: SharedViewModel, onBack: () -> Unit) {
    val videoUrl by viewModel.videoUrl.collectAsStateWithLifecycle()
    val storyId by viewModel.storyId.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }


    LaunchedEffect(videoUrl) {
        videoUrl?.let {
            val mediaItem = MediaItem.fromUri(it)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(10.dp)
    ) {
        TextButton(
            onClick = { onBack() },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Back", fontSize = 30.sp)
        }

        Text(
            text = "Preview",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp),
            fontSize = 42.sp
        )

        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )

        Button(
            onClick = {
                videoUrl?.let { url ->
                    try {
                        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                        val fileName = "Story-${storyId ?: System.currentTimeMillis()}.mp4"

                        val request = DownloadManager.Request(url.toUri())
                            .setTitle(fileName)
                            .setDescription("Downloading your story video...")
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, fileName)
                            .setAllowedOverMetered(true)

                        downloadManager.enqueue(request)
                        Toast.makeText(context, "Download started. Check notifications for progress.", Toast.LENGTH_LONG).show()

                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to start download: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } ?: run {
                    Toast.makeText(context, "Video URL not available, cannot start download.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Export Video")
        }
    }
}
