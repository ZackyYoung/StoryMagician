package com.bytedance.storymagician.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytedance.storymagician.*
import com.google.gson.JsonObject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Sealed interface to represent the UI state for the story creation process
sealed interface CreateStoryUiState {
    object Idle : CreateStoryUiState
    object Loading : CreateStoryUiState
    data class Error(val message: String) : CreateStoryUiState
}

class SharedViewModel : ViewModel() {
    private val _storyId = MutableStateFlow<Int?>(null)
    val storyId = _storyId.asStateFlow()

    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories = _stories.asStateFlow()

    private val _videoUrl = MutableStateFlow<String?>(null)
    val videoUrl = _videoUrl.asStateFlow()

    private val _shots = MutableStateFlow<List<Shot>>(emptyList())
    val shots = _shots.asStateFlow()

    private val _selectedShot = MutableStateFlow<Shot?>(null)
    val selectedShot = _selectedShot.asStateFlow()

    // State for the creation process UI
    private val _createStoryUiState = MutableStateFlow<CreateStoryUiState>(CreateStoryUiState.Idle)
    val createStoryUiState = _createStoryUiState.asStateFlow()

    private val appService: AppService = ServiceCreator.create()

    fun createStory(createStoryRequest: CreateStoryRequest) {
        viewModelScope.launch {
            // Set state to Loading to show progress bar on the UI
            _createStoryUiState.value = CreateStoryUiState.Loading
            try {
                val responseJson = appService.createStory(createStoryRequest)
                val newStoryId = responseJson.get("story_id").asInt

                Log.d("SharedViewModel", "Story task submitted with ID: $newStoryId. Starting to poll for shots.")
                pollForShots(newStoryId)

            } catch (e: Exception) {
                Log.e("SharedViewModel", "Failed to create story", e)
                _createStoryUiState.value = CreateStoryUiState.Error("Failed to submit story creation task.")
            }
        }
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
                        _storyId.value = storyId // This will trigger navigation
                        _createStoryUiState.value = CreateStoryUiState.Idle // Hide progress bar
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
        _createStoryUiState.value = CreateStoryUiState.Error("Loading storyboard timed out. Please try again later.")
    }
    
    fun dismissCreateStoryError() {
        _createStoryUiState.value = CreateStoryUiState.Idle
    }


    fun fetchShots(storyId: Int) {
        viewModelScope.launch {
            try {
                _shots.value = appService.getShots(storyId)
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Failed to fetch shots", e)
            }
        }
    }

    fun fetchShotDetails(shotId: Int) {
        viewModelScope.launch {
            try {
                _selectedShot.value = appService.getShot(shotId)
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Failed to fetch shot details", e)
            }
        }
    }

    fun updateShot(updatedShotData: Shot) {
        viewModelScope.launch {
            try {
                appService.postShot(updatedShotData)
                fetchShotDetails(updatedShotData.id)
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Failed to update shot", e)
            }
        }
    }

    fun generatePreviewVideo(storyId: Int) {
        viewModelScope.launch {
            try {
                val response = appService.getPreview(storyId)
                _videoUrl.value = response
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Failed to generate preview video", e)
            }
        }
    }

    fun fetchStories() {
        viewModelScope.launch {
            try {
                _stories.value = appService.getStories()
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Failed to fetch stories", e)
            }
        }
    }

    fun selectStory(id: Int) {
        _storyId.value = id
        _videoUrl.value = null
    }

    fun selectShot(id: Int) {
        _selectedShot.value = null
        fetchShotDetails(id)
    }
}
