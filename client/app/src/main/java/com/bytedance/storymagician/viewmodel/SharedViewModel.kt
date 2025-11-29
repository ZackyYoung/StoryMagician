package com.bytedance.storymagician.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A shared ViewModel to hold and manage data across different navigation graphs.
 * This allows communication and state sharing between the Create and Assets flows.
 */
class SharedViewModel : ViewModel() {
    // Private MutableStateFlow to hold the story ID, nullable
    private val _storyId = MutableStateFlow<Int?>(null)
    // Public StateFlow to observe the story ID from UI
    val storyId = _storyId.asStateFlow()

    // Private MutableStateFlow to hold the shot ID, nullable
    private val _shotId = MutableStateFlow<Int?>(null)
    // Public StateFlow to observe the shot ID from UI
    val shotId = _shotId.asStateFlow()

    /**
     * Updates the current story ID.
     * @param id The ID of the selected story.
     */
    fun selectStory(id: Int) {
        _storyId.value = id
    }

    /**
     * Updates the current shot ID.
     * @param id The ID of the selected shot.
     */
    fun selectShot(id: Int) {
        _shotId.value = id
    }
}
