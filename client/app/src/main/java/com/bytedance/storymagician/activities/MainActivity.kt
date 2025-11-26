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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StoryFlowApp()
        }
    }
}

@Composable
fun StoryFlowApp() {
    val navController = rememberNavController()

    // 当前页面状态，用于底部导航栏选中
    var currentRoute by remember { mutableStateOf("storyboard") }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = currentRoute
            ) { route ->
                currentRoute = route
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AppNavHost(navController = navController) { route ->
                currentRoute = route
            }
        }
    }
}

/**
 * 全局底部导航栏
 */
@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    currentRoute: String,
    onRouteSelected: (String) -> Unit
) {
    NavigationBar {
        // Create 按钮直接跳转到 storyboard
        NavigationBarItem(
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            label = { Text("Create") },
            selected = currentRoute == "create",
            onClick = {
                onRouteSelected("create")
                navController.navigate("storyboard") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                    launchSingleTop = true
                }
            }
        )

        // Assets页面
        NavigationBarItem(
            icon = { Icon(Icons.Default.Folder, contentDescription = null) },
            label = { Text("Assets") },
            selected = currentRoute == "assets",
            onClick = {
                onRouteSelected("assets")
                navController.navigate("assets") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                    launchSingleTop = true
                }
            }
        )
    }
}


/**
 * 全局 NavHost，管理所有页面的路由
 */
@Composable
fun AppNavHost(navController: NavHostController, onRouteChanged: (String) -> Unit) {
    NavHost(navController = navController, startDestination = "storyboard") {

        // Assets 页面
        composable("assets") {
            onRouteChanged("assets")
            AssetsScreen { story ->
                // 点击某个Story记录进入PreviewScreen
                navController.navigate("preview/${story.title}")
            }
        }

        // Preview 页面
        composable("preview/{title}") { backStackEntry ->
            onRouteChanged("preview")
            val title = backStackEntry.arguments?.getString("title") ?: ""
            PreviewScreen(
                storyTitle = title,
                onBack = { navController.popBackStack() } // 返回AssetsScreen
            )
        }

        // 故事板页面
        composable("storyboard") {
            onRouteChanged("storyboard")
            StoryboardScreen { shotId ->
                navController.navigate("shot_detail/$shotId")
            }
        }

        // Shot详情页
        composable("shot_detail/{id}") { backStackEntry ->
            onRouteChanged("shot_detail")
            val id = backStackEntry.arguments?.getString("id")
            ShotDetailScreen(navController = navController, shotId = id)
        }
    }
}
