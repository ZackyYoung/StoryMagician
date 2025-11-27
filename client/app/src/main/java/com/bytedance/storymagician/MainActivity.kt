package com.bytedance.storymagician

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bytedance.storymagician.components.AssetsScreen
import com.bytedance.storymagician.components.BottomNavigationBar
import com.bytedance.storymagician.components.FrontPageScreen
import com.bytedance.storymagician.components.PreviewScreen
import com.bytedance.storymagician.components.ShotDetailScreen
import com.bytedance.storymagician.components.StoryboardScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StoryMagicianApp()
        }
    }
}

@Composable
fun StoryMagicianApp() {
    val navController = rememberNavController()

    var showWarningDialog by remember { mutableStateOf(false) }
    // 当前底部导航栏选中项
    var currentNavBarItem by remember { mutableStateOf("create") }

    // 目标导航项
    var targetNavItem by remember { mutableStateOf("") }

    val executeNavigation = { item: String ->
        if (currentNavBarItem != item) {
            currentNavBarItem = item
            // 切换到对应组的最后访问页面
            when (item) {
                "create" -> {
                    navController.navigate("front_page") {
                        launchSingleTop = true
                    }
                }

                "assets" -> {
                    navController.navigate("assets") {
                        launchSingleTop = true
                    }
                }
            }
        }
    }
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentNavBarItem = currentNavBarItem,
                onNavBarItemSelected = { item ->
                    if (item == "assets" && currentNavBarItem == "create") {
                        showWarningDialog = true
                        targetNavItem = item
                    } else {
                        executeNavigation(item)
                    }
                }
            )
        }
    )
    { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AppNavHost(
                navController = navController,
                onRouteChanged = {}
            )
        }

        if (showWarningDialog) {
            AlertDialog(
                onDismissRequest = {
                    showWarningDialog = false
                },
                title = { Text("Warning") },
                text = { Text("If you switch to the asset library, your inputs in the current story will be discarded.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showWarningDialog = false
                            executeNavigation(targetNavItem)
                        }
                    ) {
                        Text("Continue")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showWarningDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}





@Preview
@Composable
fun AppNavHostPreview() {
    val navController = rememberNavController()
    AppNavHost(navController = navController, onRouteChanged = {})
}
