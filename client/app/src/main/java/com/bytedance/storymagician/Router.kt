package com.bytedance.storymagician

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

@Composable
fun CreateNavHost(viewModel: SharedViewModel) {
    val navController = rememberNavController()


    NavHost(navController = navController, startDestination = "front_page") {
        composable("front_page") {
            FrontPageScreen(
                viewModel = viewModel,
                onGenerateStoryBoard = { navController.navigate("storyboard") })
        }
        composable("storyboard") {
            StoryboardScreen(
                viewModel = viewModel,
                onShotClick = { shotId ->
                    viewModel.selectShot(shotId)
                    navController.navigate("shot_detail")
                },
                onBack = { navController.popBackStack() },
                onFinishGeneration = { navController.popBackStack() }
            )
        }
        composable("shot_detail") {
            ShotDetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun AssetsNavHost(viewModel: SharedViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "assets") {

        composable("assets") {
            LaunchedEffect(Unit) { // 使用 Unit 作为 key，确保这个 effect 只执行一次
                viewModel.fetchStories()
            }
            AssetsScreen(viewModel = viewModel) { storyId ->
                viewModel.getPreviewVideo(storyId)
                navController.navigate("preview")
            }
        }

        composable("preview") {
            PreviewScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
