package com.bytedance.storymagician

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.Observer // <--- 新增导入
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalLifecycleOwner // <--- 新增导入
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

        // =========================================================
        //                 🚀 Storyboard 路由修改
        // =========================================================
        composable("storyboard") { navBackStackEntry -> // 必须接收 navBackStackEntry
            val storyId by viewModel.storyId.collectAsStateWithLifecycle()

            // 1. 存储从 StoryboardScreen 传入的刷新回调函数
            val refreshCallback = remember {
                mutableStateOf<((Boolean) -> Unit)?>(null)
            }

            // 2. 使用 LaunchedEffect 监听 ShotDetailScreen 返回的结果
            val lifecycleOwner = LocalLifecycleOwner.current

            LaunchedEffect(lifecycleOwner, navBackStackEntry) {
                // 监听前一个路由（ShotDetailScreen）通过 SavedStateHandle 传递的布尔值
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.getLiveData<Boolean>("refresh_storyboard")
                    ?.observe(lifecycleOwner, Observer { shouldRefresh ->
                        if (shouldRefresh != null) {
                            // 执行 StoryboardScreen 传递给 onShotClick 的回调
                            refreshCallback.value?.invoke(shouldRefresh)

                            // 清除 LiveData 和回调，避免重复触发
                            navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>("refresh_storyboard")
                            refreshCallback.value = null
                        }
                    })
            }

            StoryboardScreen(
                storyId = storyId ?: 0,
                // *** 关键修改：适配新的双参数签名 (shotId, onBackFromDetail) ***
                onShotClick = { shotId, onBackFromDetail ->
                    // 存储回调函数，等待 ShotDetailScreen 返回时调用
                    refreshCallback.value = onBackFromDetail

                    // 导航前先更新 ViewModel
                    viewModel.selectShot(shotId)

                    // 执行导航
                    navController.navigate("shot_detail")
                },
                onBack = { navController.popBackStack() },
                onGenerateVideo = {
                    navController.navigate("preview")
                }
            )
        }

        // =========================================================
        //                🚀 ShotDetail 路由修改
        // =========================================================
        composable("shot_detail") {
            val shotId by viewModel.shotId.collectAsStateWithLifecycle()

            ShotDetailScreen(
                shotId = shotId ?: 0,
                // *** 关键修改：onBack 接收 Boolean 参数，并将结果存入 SavedStateHandle ***
                onBack = { shouldRefresh ->
                    // 1. 将 shouldRefresh 的结果存储到前一个堆栈条目（Storyboard）的 SavedStateHandle 中
                    navController.previousBackStackEntry
                        ?.savedStateHandle?.set("refresh_storyboard", shouldRefresh)

                    // 2. 弹出当前页面
                    navController.popBackStack()
                }
            )
        }

        composable("preview") {
            val storyId by viewModel.storyId.collectAsStateWithLifecycle()

            PreviewScreen(
                storyId = storyId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

// AssetsNavHost 保持不变
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
                storyId = storyId ?: 0,
                onBack = { navController.popBackStack() }
            )
        }
    }
}