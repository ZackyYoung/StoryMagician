package com.bytedance.storymagician

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bytedance.storymagician.components.BottomNavigationBar
import com.bytedance.storymagician.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初始化 ServiceCreator
        ServiceCreator.init(this)
        setContent {
            StoryMagicianApp()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoryMagicianApp() {
    val sharedViewModel: SharedViewModel = viewModel()
    val pagerState = rememberPagerState { 2 }
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentNavBarItem = if (pagerState.currentPage == 0) "create" else "assets",
                onNavBarItemSelected = { item ->
                    val page = if (item == "create") 0 else 1
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(page)
                    }
                }
            )
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            userScrollEnabled = false
        ) { page ->
            // 2. 将同一个 ViewModel 实例传递给两个导航容器
            when (page) {
                0 -> CreateNavHost(viewModel = sharedViewModel)
                1 -> AssetsNavHost(viewModel = sharedViewModel)
            }
        }
    }
}
