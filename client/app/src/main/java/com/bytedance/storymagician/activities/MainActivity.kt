package com.bytedance.storymagician.activities

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
import com.bytedance.storymagician.activities.AssetsActivity
import com.bytedance.storymagician.activities.FrontPageActivity
import com.bytedance.storymagician.activities.PreviewActivity
import com.bytedance.storymagician.activities.ShotDetailActivity
import com.bytedance.storymagician.activities.StoryboardActivity

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
                navController = navController,
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

/**
 * 全局底部导航栏
 */
@Composable
fun BottomNavigationBar(
    navController: NavHostController,
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


/**
 * 全局 NavHost，管理所有页面的路由
 */
@Composable
fun AppNavHost(navController: NavHostController, onRouteChanged: (String) -> Unit) {
    NavHost(navController = navController, startDestination = "front_page") {
        // 首页
        composable("front_page") {
            onRouteChanged("front_page")
            FrontPageActivity(onGenerateStoryboard = {
                // 点击Generate Storyboard按钮导航到storyboard页面
                navController.navigate("storyboard") {
                    launchSingleTop = true
                }
            })
        }
        // 故事板页面
        composable("storyboard") {
            onRouteChanged("storyboard")
            StoryboardActivity(
                navController = navController,
                onShotClick = { shotId -> navController.navigate("shot_detail/$shotId") },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Shot详情页
        composable("shot_detail/{id}") { backStackEntry ->
            onRouteChanged("shot_detail")
            val id = backStackEntry.arguments?.getString("id")
            ShotDetailActivity(shotId = id, onBack = {
                navController.popBackStack()
            })
        }

        // Assets 页面
        composable("assets") {
            onRouteChanged("assets")
            AssetsActivity { storyId ->
                // 点击某个Story记录进入PreviewScreen
                navController.navigate("preview/${storyId}")
            }
        }

        // Preview 页面
        composable("preview/{id}") { backStackEntry ->
            onRouteChanged("preview")
            val title = backStackEntry.arguments?.getString("id") ?: ""
            PreviewActivity(
                storyId = id,
                onBack = { navController.popBackStack() } // 返回AssetsScreen
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
