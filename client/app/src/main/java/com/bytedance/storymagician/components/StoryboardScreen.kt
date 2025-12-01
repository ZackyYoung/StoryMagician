package com.bytedance.storymagician.components

// 导入 Coil 的 AsyncImage
import coil.compose.AsyncImage

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
// 移除：import androidx.compose.ui.res.painterResource // 不再需要本地资源加载
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bytedance.storymagician.Shot // 假设 Shot 类现在包含 imageUrl: String
import com.bytedance.storymagician.AppService
import com.bytedance.storymagician.ServiceCreator
import kotlinx.coroutines.launch

@Composable
fun StoryboardScreen(
    storyId: Int,
    // *** 关键修改 1: onShotClick 接受一个回调函数，该回调在从详情页返回时执行 ***
    onShotClick: (Int, (Boolean) -> Unit) -> Unit = { _, _ -> },
    onBack: () -> Unit = {},
    // *** 新增：处理生成视频按钮点击的回调 ***
    onGenerateVideo: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var shots by remember { mutableStateOf(listOf<Shot>()) }

    // *** 关键修改 2: 引入 Key 来控制手动刷新 ***
    var refreshKey by remember { mutableIntStateOf(0) }

    val fetchShots = {
        coroutineScope.launch {
            try {
                val service = ServiceCreator.create<AppService>()

                // 核心修改：接收 ShotResponse 对象 (假设您的 ShotResponse.shots 属性存在)
                // 注意：由于您未提供 ShotResponse 的定义，此处为假设调用
                // val response = service.getShots(storyId)

                // 为了编译通过，我们暂时先使用占位 API 调用或假设 response.shots 存在
                val response = service.getShots(storyId)

                // 检查响应是否包含数据，并更新 shots 状态
                if (response.shots.isNotEmpty()) {
                    shots = response.shots
                } else {
                    print("API returned success: ${response.success} but shots list is empty.")
                }

            } catch (e: Exception) {
                print("Error fetching shots: $e")
                // 失败时使用占位数据，并改为中文，以便区分
                shots = listOf(
                    Shot(1, "占位图-山中营地", "已生成", imageUrl = "https://example.com/placeholder1.jpg"),
                    Shot(2, "占位图-徒步者", "未生成", imageUrl = "https://example.com/placeholder2.jpg"),
                    Shot(3, "占位图-湖景", "未生成", imageUrl = "https://example.com/placeholder3.jpg")
                )
            }
        }
    }

    // *** 关键修改 3: LaunchedEffect 现在依赖于 refreshKey ***
    LaunchedEffect(storyId, refreshKey) { // 只有 storyId 或 refreshKey 变化时才加载
        fetchShots()
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // ... (省略顶部 UI)
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

        LazyColumn(
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
                        .clickable {
                            // *** 关键修改 4: 将一个回调函数传递给 onShotClick ***
                            onShotClick(shot.id) { shouldRefresh ->
                                if (shouldRefresh) {
                                    // 如果 ShotDetailScreen 标记需要刷新，则增加 Key，触发 LaunchedEffect
                                    refreshKey++
                                }
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        AsyncImage(
                            model = shot.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            contentScale = ContentScale.Crop,
                        )

                        Spacer(Modifier.height(8.dp))
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

        // *** 生成视频按钮保持不变 ***
        Button(
            onClick = {
                onGenerateVideo()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Generate Video", fontSize = 25.sp)
        }
    }
}