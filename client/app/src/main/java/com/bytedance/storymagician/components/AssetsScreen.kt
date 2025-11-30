package com.bytedance.storymagician.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.bytedance.storymagician.Story
import com.bytedance.storymagician.viewmodel.SharedViewModel

@Composable
fun AssetsScreen(viewModel: SharedViewModel, onStoryClick: (Int) -> Unit) {
    var searchText by remember { mutableStateOf("") }
    val allStories by viewModel.stories.collectAsStateWithLifecycle()

    // Filter stories based on search text
    val filteredStories = remember(searchText, allStories) {
        if (searchText.isBlank()) {
            allStories
        } else {
            allStories.filter {
                it.title.contains(searchText, ignoreCase = true)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Text(
                text = "StoryFlow",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
            )

            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search your stories...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredStories) { story ->
                    StoryCard(story) {
                        onStoryClick(story.id)
                    }
                }
            }
        }
    }
}

@Composable
fun StoryCard(story: Story, onClick: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Use AsyncImage to load the image from the URL
            AsyncImage(
                model = story.coverRes,
                contentDescription = story.title, // Provide a meaningful content description
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop // Crop the image to fill the bounds
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = story.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = story.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
