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
    val date: String,
    val coverRes: String = "" // <--- 更改: 存储后端传来的封面图 URL // 将来这里可能改成后端传来的封面图URL或本地缓存路径
)
