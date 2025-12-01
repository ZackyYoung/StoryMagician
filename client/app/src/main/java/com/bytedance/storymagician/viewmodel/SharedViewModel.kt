package com.bytedance.storymagician.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytedance.storymagician.AppService
import com.bytedance.storymagician.CreateStoryRequest
import com.bytedance.storymagician.ServiceCreator
import com.bytedance.storymagician.Story
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {
    private val _storyId = MutableStateFlow<Int?>(null)
    val storyId = _storyId.asStateFlow()

    private val _shotId = MutableStateFlow<Int?>(null)
    val shotId = _shotId.asStateFlow()

    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories = _stories.asStateFlow()

    private val _videoUrl = MutableStateFlow<String?>(null)
    val videoUrl = _videoUrl.asStateFlow()

    private val appService: AppService = ServiceCreator.create()

    fun fetchStories() {
        viewModelScope.launch {
            try {
                _stories.value = appService.getStories()
                Log.d("SharedViewModel", "Successfully fetched stories.")
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Failed to fetch stories", e)
                _stories.value = emptyList()
            }
        }
    }

    fun fetchVideoUrl() {
        viewModelScope.launch {
            try {
                // Assuming getPreview returns a class/object that contains the video URL
                val response = appService.getPreview(storyId.value ?: 0)
                _videoUrl.value = response // Adjust based on your actual response structure
                Log.d("SharedViewModel", "Fetched video URL: $response")
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Failed to fetch video URL", e)
                _videoUrl.value = null
            }
        }
    }

    fun createStory(createStoryRequest: CreateStoryRequest) {
        viewModelScope.launch {
            try {
                val response = appService.postStory(createStoryRequest)
                _storyId.value = response
                Log.d("SharedViewModel", "Successfully created story with ID: $response")
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Failed to create story", e)
            }
        }
    }

    fun selectStory(id: Int) {
        _storyId.value = id
    }

    fun selectShot(id: Int) {
        _shotId.value = id
    }
}
