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

    // Holds the list of stories for the Assets screen
    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories = _stories.asStateFlow()

    private val appService: AppService = ServiceCreator.create()

    /**
     * Fetches the list of stories from the backend.
     */
    fun fetchStories() {
        viewModelScope.launch {
            try {
                _stories.value = appService.getStories()
                Log.d("SharedViewModel", "Successfully fetched stories.")
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Failed to fetch stories", e)
                // In a real app, you might want to expose an error state to the UI
                _stories.value = emptyList() // Clear list on error
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
