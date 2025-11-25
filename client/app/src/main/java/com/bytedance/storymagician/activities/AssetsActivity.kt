package com.bytedance.storymagician.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AssetsScreen() {
    var searchText by remember { mutableStateOf("") }

    // Box布局
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 主要内容区域（可滚动）
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

            // 故事列表（可滚动）
            LazyColumn(
                modifier = Modifier.weight(1f), // 占据剩余空间
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(getStories()) { story ->
                    StoryCard(story)
                }
            }
        }

        // 固定在底部的create和assets
        Row(
            modifier = Modifier
                .fillMaxWidth()//充满父容器
                .align(Alignment.BottomCenter) // 固定在底部中央
                .padding(10.dp) // 添加内边距
        ) {
            // Create按钮 - 白色背景，添加图标
            Button(
                onClick = { /* 创建逻辑 */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE8F5E8),
                    contentColor = Color(0xFF2E7D32)
                ),
                shape = RectangleShape,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "创建",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create")
            }

            // Assets按钮
            Button(
                onClick = { /* 等后端 */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE8F5E8),
                    contentColor = Color(0xFF2E7D32)
                ),
                shape = RectangleShape,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = "资源库",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Assets")
            }
        }
    }
}

@Composable
fun StoryCard(story: Story) {
    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 故事预览图
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE0E0E0)) // 添加背景色
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

data class Story(
    val title: String,
    val date: String
)

fun getStories(): List<Story> {
    return listOf(
        Story("Camping Adventure", "Apr 21, 2024"),
        Story("Sunset at the Summit", "Apr 21, 2024"),
        Story("Journey Through Woods", "Apr 20, 2024"),
        Story("Mountain Hiking", "Apr 19, 2024"),
        Story("Forest Exploration", "Apr 18, 2024"),
        Story("Beach Vacation", "Apr 17, 2024"),
        Story("City Tour", "Apr 16, 2024"),
        Story("Night Photography", "Apr 15, 2024"),
        Story("Winter Sports", "Apr 14, 2024"),
        Story("Spring Festival", "Apr 13, 2024")
        //这些是样例，到时候应该是数据库导入进来的
    )
}