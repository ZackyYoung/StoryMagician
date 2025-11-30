package com.bytedance.storymagician


data class CreateStoryRequest(
    val title: String,
    val description: String,
    val style: String
)

data class Shot(
    val id: Int,
    val title: String,
    val status: String = "Not Generated",
    val imageRes: Int = R.drawable.placeholder, // 将来这里可能改成后端传来的缩略图URL或本地缓存路径
    val description: String = "",
    val transition: String = "",
    val narration: String = ""
)

data class Story(
    val id: Int,
    val title: String,
    val date: String,
    val coverRes: Int = R.drawable.placeholder // 将来这里可能改成后端传来的封面图URL或本地缓存路径
)
