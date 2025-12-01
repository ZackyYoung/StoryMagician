package com.bytedance.storymagician

import com.google.gson.annotations.SerializedName


data class CreateStoryRequest(
    val title: String,
    val description: String,
    val style: String
)

// 新增：ShotResponse 类，匹配后端返回的 {"success":..., "shots":[...]} 结构
data class ShotResponse(
    val success: Boolean,
    val shots: List<Shot>
)

data class Shot(
    val id: Int,
    val title: String,
    val status: String = "Not Generated",

    // 后端返回的是 imageRes，使用 @SerializedName 映射到 imageUrl
    @SerializedName("imageRes")
    val imageUrl: String = "", // 将来这里可能改成后端传来的缩略图URL或本地缓存路径
    val description: String = "",
    val transition: String = "",
    val narration: String = ""
)

data class Story(
    val id: Int,
    val title: String,
    val date: String,
    val coverUrl: String = "" // <--- 更改: 存储后端传来的封面图 URL // 将来这里可能改成后端传来的封面图URL或本地缓存路径
)
