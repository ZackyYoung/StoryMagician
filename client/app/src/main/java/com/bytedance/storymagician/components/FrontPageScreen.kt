package com.bytedance.storymagician.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.bytedance.storymagician.CreateStoryRequest
import com.bytedance.storymagician.viewmodel.UiState
import com.bytedance.storymagician.viewmodel.SharedViewModel

@Composable
fun FrontPageScreen(
    viewModel: SharedViewModel,
    onGenerateStoryBoard: () -> Unit = {}
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf("Movie") }
    val styles = listOf("Movie", "Animation", "Realistic")
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "StoryMagician",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "Create",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        TextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text("Enter your title...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )

        TextField(
            value = description,
            onValueChange = { description = it },
            placeholder = { Text("Write your story...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            styles.forEach { style ->
                FilterChip(
                    selected = selectedStyle == style,
                    onClick = { selectedStyle = style },
                    label = { Text(style, fontSize = 15.sp) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.createStory(CreateStoryRequest(title, description, selectedStyle))
                onGenerateStoryBoard()
            },
            enabled = uiState !is UiState.Loading, // Disable button while loading
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Generate Storyboard", fontSize = 25.sp, fontWeight = FontWeight.Medium)
        }

        Text(
            text = "The storyboard will open automatically after generation.",
            fontSize = 18.sp,
            color = Color.Gray,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
        )
    }

    // Handle UI state for loading and error
    when (val state = uiState) {
        is UiState.Loading -> {
            AlertDialog(
                onDismissRequest = { /* Cannot be dismissed */ },
                title = { Text("The content is generating", fontSize = 18.sp) },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "This may take a moment, please wait...",
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                confirmButton = {}
            )
        }

        is UiState.Error -> {
            AlertDialog(
                onDismissRequest = { viewModel.dismissAlert() },
                title = { Text("Error") },
                text = { Text(state.message, fontSize = 14.sp, textAlign = TextAlign.Center) },
                confirmButton = {
                    Button(onClick = { viewModel.dismissAlert() }) {
                        Text("OK")
                    }
                }
            )
        }

        is UiState.Success -> {
        }

        is UiState.Idle -> {
        }


    }
}

@Preview
@Composable
fun FrontPageScreenPreview() {
    // Preview won't work well with ViewModel, but this is a placeholder
    // FrontPageScreen(viewModel = //... provide a mock ViewModel for preview)
}
