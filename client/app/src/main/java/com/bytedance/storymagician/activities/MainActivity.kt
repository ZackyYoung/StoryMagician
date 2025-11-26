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
    // 保存状态的键
    private val KEY_LAST_CREATE_ROUTE = "front_page"
    private val KEY_LAST_ASSETS_ROUTE = "assets"
    private val KEY_CURRENT_NAV_ITEM = "create"

    private val KEY_LAST_SELECTED_STYLE = "movie"
    private val KEY_LAST_SHOT_ID = ""
    private val KEY_LAST_STORY_TITLE = ""

    // 保存的状态
    private var lastCreateGroupRoute by mutableStateOf("front_page")
    private var lastAssetsGroupRoute by mutableStateOf("assets")
    private var currentNavBarItem by mutableStateOf("create")
    private var lastSelectedStyle by mutableStateOf("movie")
    private var lastShotId by mutableStateOf<String?>(null)
    private var lastStoryTitle by mutableStateOf("")

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

    // 当前底部导航栏选中项
    var currentNavBarItem by remember { mutableStateOf("create") }

    // 第一组（front_page, storyboard, shot_detail）的最后访问页面
    var lastCreateGroupRoute by remember { mutableStateOf("front_page") }

    // 第二组（assets, preview）的最后访问页面
    var lastAssetsGroupRoute by remember { mutableStateOf("assets") }

    // 用于跟踪当前页面属于哪个组
    var currentGroup by remember { mutableStateOf("create") }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentNavBarItem = currentNavBarItem,
                lastCreateGroupRoute = lastCreateGroupRoute,
                lastAssetsGroupRoute = lastAssetsGroupRoute,
                onNavBarItemSelected = { item ->
                    currentNavBarItem = item
                    // 切换到对应组的最后访问页面
                    when (item) {
                        "create" -> {
                            navController.navigate(lastCreateGroupRoute) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }

                        "assets" -> {
                            navController.navigate(lastAssetsGroupRoute) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            )
        }
    )
    { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AppNavHost(
                navController = navController,
                onRouteChanged = { route ->
                    // 更新相应组的最后访问页面
                    when (route) {
                        "front_page", "storyboard", "shot_detail" -> {
                            lastCreateGroupRoute = route
                            currentGroup = "create"
                        }

                        "assets", "preview" -> {
                            lastAssetsGroupRoute = route
                            currentGroup = "assets"
                        }
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
    lastCreateGroupRoute: String,
    lastAssetsGroupRoute: String,
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
            StoryboardActivity(navController = navController) { shotId ->
                navController.navigate("shot_detail/$shotId")
            }
        }

        // Shot详情页
        composable("shot_detail/{id}") { backStackEntry ->
            onRouteChanged("shot_detail")
            val id = backStackEntry.arguments?.getString("id")
            ShotDetailActivity(navController = navController, shotId = id)
        }

        // Assets 页面
        composable("assets") {
            onRouteChanged("assets")
            AssetsActivity { story ->
                // 点击某个Story记录进入PreviewScreen
                navController.navigate("preview/${story.title}")
            }
        }

        // Preview 页面
        composable("preview/{title}") { backStackEntry ->
            onRouteChanged("preview")
            val title = backStackEntry.arguments?.getString("title") ?: ""
            PreviewActivity(
                storyTitle = title,
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
