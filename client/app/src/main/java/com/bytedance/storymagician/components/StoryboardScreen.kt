package com.bytedance.storymagician.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.bytedance.storymagician.Shot
import com.bytedance.storymagician.viewmodel.CreateStoryUiState
import com.bytedance.storymagician.viewmodel.SharedViewModel

@Composable
fun StoryboardScreen(
    viewModel: SharedViewModel,
    onShotClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    val shots by viewModel.shots.collectAsStateWithLifecycle()
    var expanded by remember { mutableStateOf(false) }
    var transition by remember { mutableStateOf("none") }
    val uiState by viewModel.createStoryUiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
            Spacer(Modifier.width(6.dp))
            Text("Back", fontSize = 20.sp)
        }

        Text(
            text = "Storyboard",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Transition Type",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Box {
            OutlinedTextField(
                value = transition,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("none") },
                    onClick = {
                        transition = "none"
                        expanded = false
                    }
                )

                DropdownMenuItem(
                    text = { Text("fade") },
                    onClick = {
                        transition = "fade"
                        expanded = false
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(shots) { shot ->
                ShotCard(shot = shot, onClick = { onShotClick(shot.id) })
            }
        }

        Button(
            onClick = {
                viewModel.generateVideo(viewModel.storyBoardStoryId.value!!, transition)
                navController.navigate("assets")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Generate Video", fontSize = 25.sp)
        }
    }
    // Handle UI state for loading and error
    when (val state = uiState) {
        is CreateStoryUiState.Loading -> {
            AlertDialog(
                onDismissRequest = { /* Cannot be dismissed */ },
                title = { Text("Generating Video") },
                text = { Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("This may take a moment, please wait...")
                } },
                confirmButton = {}
            )
        }
        is CreateStoryUiState.Error -> {
            AlertDialog(
                onDismissRequest = { viewModel.dismissCreateStoryError() },
                title = { Text("Error") },
                text = { Text(state.message) },
                confirmButton = {
                    Button(onClick = { viewModel.dismissCreateStoryError() }) {
                        Text("OK")
                    }
                }
            )
        }
        is CreateStoryUiState.Idle -> { /* Do nothing */ }
    }
}

@Composable
fun ShotCard(shot: Shot, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            AsyncImage(
                model = shot.imageRes, // URL for the shot's thumbnail
                contentDescription = shot.title,
                modifier = Modifier
                    .size(90.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    shot.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    shot.status,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
