package com.bytedance.storymagician.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytedance.storymagician.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Sealed interface to represent the UI state for the story creation process
sealed interface UiState {
    object Idle : UiState
    object Loading : UiState
    data class Error(val message: String) : UiState
    data class Success(val message: String) : UiState
}

class SharedViewModel : ViewModel() {
    private val _storyBoardStoryId = MutableStateFlow<Int?>(null)
    val storyBoardStoryId = _storyBoardStoryId.asStateFlow()

    private val _previewStoryId = MutableStateFlow<Int?>(null)
    val previewStoryId = _previewStoryId.asStateFlow()

    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories = _stories.asStateFlow()

    private val _videoUrl = MutableStateFlow<String?>(null)
    val videoUrl = _videoUrl.asStateFlow()

    private val _shots = MutableStateFlow<List<Shot>>(emptyList())
    val shots = _shots.asStateFlow()

    private val _selectedShot = MutableStateFlow<Shot?>(null)
    val selectedShot = _selectedShot.asStateFlow()

    // State for the creation process UI
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()


    private val appService: AppService = ServiceCreator.create()

    fun createStory(createStoryRequest: CreateStoryRequest) {
        viewModelScope.launch {
            // Set state to Loading to show progress bar on the UI
            _uiState.value = UiState.Loading
            try {
                val responseJson = appService.createStory(createStoryRequest)
                val newStoryId = responseJson.get("story_id").asInt
                Log.d("SharedViewModel", "Story task submitted with ID: $newStoryId. Starting to poll for shots.")
                pollForShots(newStoryId)

            } catch (e: Exception) {
                Log.e("SharedViewModel", "Failed to create story", e)
                _uiState.value = UiState.Error("Failed to submit story creation task.")
            }
        }
    }


    
    fun dismissAlert() {
        _uiState.value = UiState.Idle
    }

    fun onPreviewNavigated() {
        _previewStoryId.value = null
    }
    fun updateShot(regenerateShotRequest: RegenerateShotRequest) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                appService.postShot(regenerateShotRequest)
                pollForShots(storyBoardStoryId.value!!)
                selectShot(regenerateShotRequest.id)
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Failed to update shot", e)
            }
        }
    }

    fun generateVideo(storyId: Int, transition: String) {

        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                appService.generateVideo(storyId, transition)
                getPreviewVideo(storyId)
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Failed to generate preview video", e)
            }
        }
    }

    fun getPreviewVideo(storyId: Int) {
        viewModelScope.launch {
            try {
                pollForVideo(storyId)
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Failed to generate preview video", e)
            }
        }
    }

    fun fetchStories() {
        viewModelScope.launch {
            try {
                val allStories = appService.getStories()
                for(story in allStories) {
                    if(story.status == "done" && !_stories.value.contains(story)){
                        _stories.value += story
                    }
                }
                Log.d("SharedViewModel", "Stories fetched successfully!")
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Failed to fetch stories", e)
            }
        }
    }


    fun selectShot(id: Int) {
        _selectedShot.value = shots.value.find { it.id == id }
    }

    private suspend fun pollForShots(storyId: Int) {
        val maxRetries = 20 // Poll for up to 45 seconds (20 * 3s)
        val delayMillis = 6000L // 6 seconds

        repeat(maxRetries) { attempt ->
            Log.d("SharedViewModel", "Polling for shots... Attempt ${attempt + 1}/$maxRetries")
            try {
                val fetchedShots = appService.getShots(storyId)
                if (fetchedShots.isNotEmpty()) {
                    var flag: Boolean = true
                    for (shot in fetchedShots) {
                        if (shot.status != "done") {
                            flag = false
                            break
                        }
                    }
                    if (flag) {
                        _shots.value = fetchedShots
                        _storyBoardStoryId.value = storyId // This will trigger navigation
                        _uiState.value = UiState.Idle // Hide progress bar
                        Log.d("SharedViewModel", "Shots fetched successfully!")
                        return // Success
                    }
                }
            } catch (e: Exception) {
                Log.w("SharedViewModel", "Polling attempt ${attempt + 1} failed, retrying...", e)
            }
            delay(delayMillis)
        }

        Log.w("SharedViewModel", "Polling for shots timed out.")
        _uiState.value = UiState.Error("Loading storyboard timed out. Please try again later.")
    }

    private suspend fun pollForVideo(storyId: Int) {
        val maxRetries = 30 // Poll for up to 120 seconds (30 * 4s)
        val delayMillis = 4000L // 6 seconds
        repeat(maxRetries) { attempt ->
            Log.d("SharedViewModel", "Polling for video... Attempt ${attempt + 1}/$maxRetries")
            try {
                val videoUrl = appService.getPreview(storyId).get("video_url").asString
                if (videoUrl.isNotEmpty()) {
                    _videoUrl.value = videoUrl
                    _previewStoryId.value = storyId
                    _uiState.value = UiState.Success("Video generated successfully!You can find it in the assets.")
                    Log.d("SharedViewModel", "Video fetched successfully!")
                    return // Success
                }
            } catch (e: Exception) {
                Log.w("SharedViewModel", "Polling attempt ${attempt + 1} failed, retrying...", e)
            }
            delay(delayMillis)
        }
        Log.w("SharedViewModel", "Polling for video timed out.")
        _uiState.value =
            UiState.Error("Loading video timed out. Please try again later.")
    }
}
