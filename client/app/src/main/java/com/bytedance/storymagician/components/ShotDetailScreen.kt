package com.bytedance.storymagician.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bytedance.storymagician.viewmodel.SharedViewModel

@Composable
fun ShotDetailScreen(viewModel: SharedViewModel, onBack: () -> Unit) {
    val shot by viewModel.selectedShot.collectAsStateWithLifecycle()
    var descriptionText by remember(shot) { mutableStateOf(shot!!.description) }
    var narrationText by remember(shot){ mutableStateOf(shot!!.narration) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TextButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
            Spacer(Modifier.width(6.dp))
            Text("Back to Storyboard")
        }

        Spacer(Modifier.height(16.dp))

        if (shot == null) {
            // Show a loading indicator while the shot details are being fetched
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Display shot details once loaded
            AsyncImage(
                model = shot!!.imageRes, // The URL of the main image
                contentDescription = shot!!.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(16.dp))



            OutlinedTextField(
                value = descriptionText,
                onValueChange = { descriptionText = it },
                label = { Text("Shot Description") },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(24.dp))


            Text(text = "Narration text", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = narrationText,
                onValueChange = { narrationText = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    val updatedShot = shot!!.copy(title = descriptionText)
                    viewModel.updateShot(updatedShot)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Generate Image")
            }
        }
    }
}
