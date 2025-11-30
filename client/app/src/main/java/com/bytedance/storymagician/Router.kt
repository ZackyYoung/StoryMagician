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

@Composable
fun CreateNavHost(viewModel: SharedViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "front_page") {
        composable("front_page") {
            FrontPageScreen(onGenerateStoryboard = { createStoryRequest ->
                viewModel.createStory(createStoryRequest)
                navController.navigate("storyboard")
            })
        }
        composable("storyboard") {
            val storyId by viewModel.storyId.collectAsStateWithLifecycle()
            StoryboardScreen(
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
                shotId = shotId ?: 0,
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
            viewModel.fetchStories()
            AssetsScreen(viewModel = viewModel) { storyId ->
                viewModel.selectStory(storyId)
                navController.navigate("preview")
            }
        }
        composable("preview") {
            val storyId by viewModel.storyId.collectAsStateWithLifecycle()
            PreviewScreen(
                storyId = storyId ?: 0,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
