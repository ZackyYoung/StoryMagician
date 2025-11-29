package com.bytedance.storymagician

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bytedance.storymagician.components.AssetsScreen
import com.bytedance.storymagician.components.FrontPageScreen
import com.bytedance.storymagician.components.PreviewScreen
import com.bytedance.storymagician.components.ShotDetailScreen
import com.bytedance.storymagician.components.StoryboardScreen
import com.bytedance.storymagician.viewmodel.SharedViewModel

/**
 * "Create" 标签页的独立导航容器
 * @param viewModel 共享的 ViewModel，用于跨屏幕传递数据
 */
@Composable
fun CreateNavHost(viewModel: SharedViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "front_page") {
        composable("front_page") {
            FrontPageScreen(onGenerateStoryboard = {
                // 在实际应用中，这里可能会调用 API 生成一个故事，然后获取其 ID
                val generatedStoryId = 1 // 假设生成的故事ID是1
                viewModel.selectStory(generatedStoryId)
                navController.navigate("storyboard")
            })
        }
        composable("storyboard") {
            val storyId by viewModel.storyId.collectAsStateWithLifecycle()
            StoryboardScreen(
                // 如果 storyId 为 null，显示0。在真实应用中，您可能想显示一个加载或错误状态。
                storyId = storyId ?: 0,
                onShotClick = { shotId ->
                    viewModel.selectShot(shotId)
                    navController.navigate("shot_detail")
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("shot_detail") {
            val shotId by viewModel.shotId.collectAsStateWithLifecycle()
            ShotDetailScreen(
                // 如果 shotId 为 null，显示0。
                shotId = shotId ?: 0,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * "Assets" 标签页的独立导航容器
 * @param viewModel 共享的 ViewModel，用于跨屏幕传递数据
 */
@Composable
fun AssetsNavHost(viewModel: SharedViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "assets") {
        composable("assets") {
            AssetsScreen { storyId ->
                viewModel.selectStory(storyId)
                navController.navigate("preview")
            }
        }
        composable("preview") {
            val storyId by viewModel.storyId.collectAsStateWithLifecycle()
            PreviewScreen(
                // 如果 storyId 为 null，显示0。
                storyId = storyId ?: 0,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
