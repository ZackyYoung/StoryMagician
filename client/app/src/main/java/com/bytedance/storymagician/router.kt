package com.bytedance.storymagician

import android.os.Bundle
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

    val pageStates: Bundle = Bundle()


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
                onShotClick = { shotId -> pageStates.putInt("shot_id", shotId)
                    navController.navigate("shot_detail") },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Shot详情页
        composable("shot_detail") {
            onRouteChanged("shot_detail")
            val shotId = pageStates.getInt("shot_id")
            ShotDetailScreen(shotId = shotId, onBack = {
                navController.popBackStack()
            })
        }

        // Assets 页面
        composable("assets") {
            onRouteChanged("assets")
            AssetsScreen { storyId ->
                pageStates.putInt("story_id", storyId)
                navController.navigate("preview")
            }
        }

        // Preview 页面
        composable("preview") {
            onRouteChanged("preview")
            val storyId = pageStates.getInt("story_id")
            PreviewScreen(
                storyId = storyId,
                onBack = { navController.popBackStack() } // 返回AssetsScreen
            )
        }





    }
}