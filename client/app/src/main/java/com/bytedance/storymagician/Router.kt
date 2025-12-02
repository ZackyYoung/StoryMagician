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
    val storyId by viewModel.storyId.collectAsStateWithLifecycle()
    val videoUrl by viewModel.videoUrl.collectAsStateWithLifecycle()

    // Navigate to storyboard when a story is created/selected
    LaunchedEffect(storyId) {
        storyId?.let {
            if (navController.currentDestination?.route != "storyboard") {
                navController.navigate("storyboard")
            }
        }
    }

    // Navigate to preview when a video URL is ready
    LaunchedEffect(videoUrl) {
        videoUrl?.let { navController.navigate("preview") }
    }

    NavHost(navController = navController, startDestination = "front_page") {
        composable("front_page") {
            FrontPageScreen(viewModel = viewModel)
        }
        composable("storyboard") {
            StoryboardScreen(
                viewModel = viewModel,
                onShotClick = { shotId ->
                    viewModel.selectShot(shotId)
                    navController.navigate("shot_detail")
                },
                onBack = { navController.popBackStack() },
                onGenerateVideo = { storyId?.let { viewModel.generatePreviewVideo(it) } }
            )
        }
        composable("shot_detail") {
            ShotDetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() } 
            )
        }
        composable("preview") {
            PreviewScreen(
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
            AssetsScreen(viewModel = viewModel) { storyId ->
                viewModel.selectStory(storyId)
            }
        }
    }
}
