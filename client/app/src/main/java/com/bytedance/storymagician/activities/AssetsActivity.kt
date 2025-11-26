package com.bytedance.storymagician.activities

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bytedance.storymagician.Story

@Composable
fun AssetsActivity(onStoryClick: (Int) -> Unit) {
    var searchText by remember { mutableStateOf("") }

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
                items(getStories()) { story ->
                    StoryCard(story) {
                        onStoryClick(story.id) // 点击回调
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
            .clickable { onClick() } // 整个Card可点击
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE0E0E0))
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



fun getStories(): List<Story> {
    return listOf(
        Story(1, "Camping Adventure", "Apr 21, 2024"),
        Story(2, "Sunset at the Summit", "Apr 21, 2024"),
        Story(3, "Journey Through Woods", "Apr 20, 2024"),
        Story(4, "Mountain Hiking", "Apr 19, 2024"),
        Story(5, "Forest Exploration", "Apr 18, 2024"),
        Story(6, "Beach Vacation", "Apr 17, 2024"),
        Story(7, "City Tour", "Apr 16, 2024"),
        Story(8, "Night Photography", "Apr 15, 2024"),
        Story(9, "Winter Sports", "Apr 14, 2024"),
        Story(10, "Spring Festival", "Apr 13, 2024")
        //这些是样例，到时候应该是数据库导入进来的
    )
}