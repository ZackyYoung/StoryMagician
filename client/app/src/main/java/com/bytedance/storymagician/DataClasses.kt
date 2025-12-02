package com.bytedance.storymagician

import com.google.gson.annotations.SerializedName


data class CreateStoryRequest(
    val title: String,
    val description: String,
    val style: String
)

data class RegenerateShotRequest(
    val id: Int,
    val title: String,
    @SerializedName("prompt")
    val description: String,
    val narration: String

)

data class Shot(
    val id: Int,
    @SerializedName("scene_index")
    val sceneIndex: Int,
    @SerializedName("prompt")
    val description: String = "",
    val title: String,
    val style: String,
    val narration: String = "",
    @SerializedName("image_url")
    val imageRes: String = "",
    @SerializedName("audio_url")
    val audioRes: String = "",
    val status: String = "Not Generated",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = "",
    val info: String = "",
    val story: Int
)

data class Story(
    val id: Int,
    val title: String,
    val status: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("cover_url")
    val coverRes: String = ""
)
