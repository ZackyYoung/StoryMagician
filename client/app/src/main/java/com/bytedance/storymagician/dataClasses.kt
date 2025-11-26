package com.bytedance.storymagician

data class Shot(
    val id: Int,
    val title: String,
    val status: String = "Not Generated",
    val imageRes: Int = R.drawable.placeholder // 将来这里可能改成后端传来的缩略图URL或本地缓存路径
)

data class Story(
    val id: Int,
    val title: String,
    val date: String
)
