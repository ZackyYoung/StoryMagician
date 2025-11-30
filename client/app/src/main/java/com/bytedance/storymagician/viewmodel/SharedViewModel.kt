package com.bytedance.storymagician.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytedance.storymagician.AppService
import com.bytedance.storymagician.CreateStoryRequest
import com.bytedance.storymagician.ServiceCreator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {
    private val _storyId = MutableStateFlow<Int?>(null)
    val storyId = _storyId.asStateFlow()

    private val _shotId = MutableStateFlow<Int?>(null)
    val shotId = _shotId.asStateFlow()

    private val appService: AppService = ServiceCreator.create()

    fun createStory(createStoryRequest: CreateStoryRequest) {
        viewModelScope.launch {
            try {
                // Make the real network call to the backend service
                val response = appService.postStory(createStoryRequest)

                // Update the storyId with the ID from the server response
                _storyId.value = response
                Log.d("SharedViewModel", "Successfully created story with ID: $response")

            } catch (e: Exception) {
                // Handle exceptions, e.g., show an error message to the user
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
