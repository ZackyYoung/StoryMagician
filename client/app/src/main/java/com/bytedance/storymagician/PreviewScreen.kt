package com.bytedance.storymagician

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.ui.graphics.RectangleShape


@Composable
fun PreviewScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(10.dp)
    ) {
        // 1. Back按钮
        TextButton(
            onClick = { //等后端完善
                 },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Back",
                fontSize = 30.sp)
        }

        // 2. Preview标题
        Text(
            text = "Preview",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp),
            fontSize = 42.sp
        )

        // 3. 视频播放区域
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Black)
        ) {
            //视频播放区的后端接口
        }

        // 4. Export Video按钮
        Button(
            onClick = { //export video按钮的后端接口
                },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Export Video")
        }

        // 5. 空白区域
        Spacer(modifier = Modifier.weight(1f))

        // 6. 底部按钮 - 各占一半宽度
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Create按钮
            Button(
                onClick = { /* create的后端接口*/ },
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
                onClick = { //asset按钮的后端接口
                    },
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