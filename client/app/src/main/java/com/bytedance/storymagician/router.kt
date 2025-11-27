package com.bytedance.storymagician

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bytedance.storymagician.components.AssetsScreen
import com.bytedance.storymagician.components.FrontPageScreen
import com.bytedance.storymagician.components.PreviewScreen
import com.bytedance.storymagician.components.ShotDetailScreen
import com.bytedance.storymagician.components.StoryboardScreen

/**
 * 全局 NavHost，管理所有页面的路由
 */
@Composable
fun AppNavHost(navController: NavHostController, onRouteChanged: (String) -> Unit) {
    NavHost(navController = navController, startDestination = "front_page") {
        // 首页
        composable("front_page") {
            onRouteChanged("front_page")
            FrontPageScreen(onGenerateStoryboard = {
                // 点击Generate Storyboard按钮导航到storyboard页面
                navController.navigate("storyboard") {
                    launchSingleTop = true
                }
            })
        }
        // 故事板页面
        composable("storyboard") {
            onRouteChanged("storyboard")
            StoryboardScreen(
                onShotClick = { shotId -> navController.navigate("shot_detail/$shotId") },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Shot详情页
        composable("shot_detail/{id}") { backStackEntry ->
            onRouteChanged("shot_detail")
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            ShotDetailScreen(shotId = id, onBack = {
                navController.popBackStack()
            })
        }

        // Assets 页面
        composable("assets") {
            onRouteChanged("assets")
            AssetsScreen { storyId ->
                // 点击某个Story记录进入PreviewScreen
                navController.navigate("preview/$storyId")
            }
        }

        // Preview 页面
        composable("preview/{id}") { backStackEntry ->
            onRouteChanged("preview")
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            PreviewScreen(
                storyId = id,
                onBack = { navController.popBackStack() } // 返回AssetsScreen
            )
        }





    }
}