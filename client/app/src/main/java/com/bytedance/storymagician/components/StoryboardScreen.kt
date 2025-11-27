package com.bytedance.storymagician.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bytedance.storymagician.Shot


@Composable
fun StoryboardScreen(
    storyId: Int,
    onShotClick: (Int) -> Unit = {},
    onBack: () -> Unit = {}
) {

    // TODO: 这里将来可以从后端接口获取列表数据，例如：
    // val shots = remember { mutableStateListOf<Shot>() }
    // LaunchedEffect(Unit) {
    //     val dataFromBackend = fetchShotsFromBackend(storyId)
    //     shots.clear()
    //     shots.addAll(dataFromBackend)
    // }

    val shots = remember {
        listOf(
            Shot(1, "Camp in the Morning Fog", "Generated"),
            Shot(2, "Hikers on Trail", "Not Generated"),
            Shot(3, "Mountain Vista", "Not Generated")
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextButton(onClick = { onBack() }) {
            Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Back", fontSize = 20.sp)
        }

        Text(
            text = "Storyboard",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn (
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(shots) { shot ->

                Card(
                    modifier = Modifier
                        .width(220.dp)
                        .height(220.dp)
                        .clickable { onShotClick(shot.id) },   // ← 点击事件
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {

                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        // TODO: 这里的Image将来可以使用 Coil/Glide 加载后端传来的缩略图URL
                        Image(
                            painter = painterResource(id = shot.imageRes),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(Modifier.height(8.dp))
// TODO: 这里可以显示后端返回的标题
                        Text(
                            shot.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )

                        // TODO: 这里显示后端返回的生成状态，比如 "Generated", "Processing" 等
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
        // TODO: Generate Video 按钮点击后可以调用后端接口生成视频，并刷新StoryboardScreen
        Button(
            onClick = { /* TODO 调用后端生成Storyboard*/ },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Generate Video", fontSize = 25.sp)
        }
    }
}

@Preview
@Composable
fun StoryBoardPreview() {
    StoryboardScreen(storyId = 1)
}
