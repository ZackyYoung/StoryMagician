package com.bytedance.storymagician.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * 全局底部导航栏
 */
@Composable
fun BottomNavigationBar(
    currentNavBarItem: String,
    onNavBarItemSelected: (String) -> Unit
) {
    NavigationBar {
        // Create 按钮
        NavigationBarItem(
            icon = { Icon(Icons.Default.Add, contentDescription = "Create") },
            label = { Text("Create") },
            selected = currentNavBarItem == "create",
            onClick = {
                onNavBarItemSelected("create")
            }
        )

        // Assets按钮
        NavigationBarItem(
            icon = { Icon(Icons.Default.Folder, contentDescription = "Assets") },
            label = { Text("Assets") },
            selected = currentNavBarItem == "assets",
            onClick = {
                onNavBarItemSelected("assets")
            }
        )
    }
}