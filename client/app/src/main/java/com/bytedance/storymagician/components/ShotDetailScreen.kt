package com.bytedance.storymagician.components

import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bytedance.storymagician.ServiceCreator
import com.bytedance.storymagician.Shot
import kotlinx.coroutines.launch
import com.bytedance.storymagician.AppService


@Composable
fun ShotDetailScreen(
    shotId: Int?,
    // *** 关键修改：onBack 接受一个 Boolean 参数，表示是否需要刷新 Storyboard ***
    onBack: (Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    // 存储从后端拉取的当前状态（包括图片URL、状态等）
    var currentShot by remember { mutableStateOf<Shot?>(null) }
    var loading by remember { mutableStateOf(true) }
    var fetchError by remember { mutableStateOf(false) }

    // 跟踪是否是第一次成功加载，用于初始化可编辑文本
    val initialLoad = remember { mutableStateOf(true) }

    // 用于在提交后触发图片/状态重新加载
    var updateTrigger by remember { mutableIntStateOf(0) }

    // *** 关键：记录是否成功生成过新图片，用于通知 Storyboard 刷新 ***
    val generationCompleted = remember { mutableStateOf(false) } // <--- 新增状态

    // 可编辑的文本状态（与 currentShot 隔离）
    var description by remember { mutableStateOf("") }
    var transition by remember { mutableStateOf("") }
    var narration by remember { mutableStateOf("") }

    val fetchShot = {
        coroutineScope.launch {
            loading = true
            fetchError = false
            try {
                if (shotId != null) {
                    val service = ServiceCreator.create<AppService>()
                    val fetchedShot = service.getShot(shotId)

                    // 只有在第一次加载成功时，才将后端数据同步给可编辑状态
                    if (initialLoad.value) {
                        fetchedShot.let {
                            description = it.description
                            transition = it.transition
                            narration = it.narration
                            initialLoad.value = false // 标记初始化完成
                        }
                    }
                    // 无论如何，更新当前显示的 shot 状态 (图片和状态)
                    currentShot = fetchedShot

                }
            } catch (e: Exception) {
                e.printStackTrace()
                fetchError = true
            } finally {
                loading = false
            }
        }
    }

    // 初始加载和触发更新
    LaunchedEffect(shotId, updateTrigger) {
        fetchShot()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // 返回按钮：点击时根据是否成功生成过新图片来决定是否刷新 Storyboard
        TextButton(onClick = {
            // *** 关键修改：传递 generationCompleted 的状态给 onBack ***
            onBack(generationCompleted.value)
        }) {
            Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Back", fontSize = 20.sp)
        }

        if (loading) {
            CircularProgressIndicator(Modifier.padding(16.dp))
        } else if (fetchError || currentShot == null) {
            Text("Error loading shot details or Shot ID is missing.", color = Color.Red, modifier = Modifier.padding(16.dp))
            Button(onClick = {
                updateTrigger++ // 点击重试也会触发加载
            }) {
                Text("Retry")
            }
        } else {
            // 使用 currentShot 来显示最新的图片和 ID
            Text(
                "Shot Detail (ID = ${currentShot!!.id})",
                fontSize = 32.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // *** 使用 AsyncImage 显示图片 (来自 currentShot.imageUrl) ***
            AsyncImage(
                model = currentShot!!.imageUrl, // <--- 显示最新拉取的图片
                contentDescription = "Shot Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color.LightGray, RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Shot description", fontSize = 16.sp)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Text("Video Transition", fontSize = 16.sp)
            var expanded by remember { mutableStateOf(false) }
            Box {
                OutlinedTextField(
                    value = transition,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            // 修正：这里应该使用 Icons.Filled.ArrowDropDown 或类似的，而不是 ArrowBack
                            Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                        }
                    }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("Ken Burns Effect", "Fade In", "Crossfade", "Volume Mix").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                transition = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Narration text", fontSize = 16.sp)
            OutlinedTextField(
                value = narration,
                onValueChange = { narration = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        currentShot?.let { originalShot ->
                            val service = ServiceCreator.create<AppService>()
                            // 创建包含用户编辑后文本的 Shot 对象
                            val updatedShot = originalShot.copy(
                                description = description,
                                transition = transition,
                                narration = narration
                            )
                            try {
                                // 提交更新到后端
                                service.postShot(updatedShot)

                                // *** 关键：提交成功，标记为已生成，并触发本地刷新 ***
                                generationCompleted.value = true
                                updateTrigger++

                            } catch (e: Exception) {
                                e.printStackTrace()
                                // 如果提交失败，我们不标记 generationCompleted
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Generate Image", fontSize = 25.sp)
            }
        }
    }
}