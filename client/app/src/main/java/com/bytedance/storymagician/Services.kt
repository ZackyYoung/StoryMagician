package com.bytedance.storymagician

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AppService {
    // 生成一个新的story
    @POST("/story")
    suspend fun postStory(@Body createStoryRequest: CreateStoryRequest): Story

    // 获取所有的story
    @GET("/story")
    suspend fun getStories(): List<Story>

    // 通过storyId获取一个story相关的内容，包括所有的Shot（简要形式）
    @GET("/story/{storyId}")
    suspend fun getStory(@Path("storyId") storyId: Int): Story

    // 通过storyId删除一个story
    @DELETE("/story/{storyId}")
    suspend fun deleteStory(@Path("storyId") storyId: Int): Response<ResponseBody>

    // 通过shotId获取一个shot的详细内容
    @GET("/shot/{shotId}")
    suspend fun getShot(@Path("shotId") shotId: Int): ShotDetail

    // 更改shot的详细内容，并返回更新后的内容， 包括 narration，transition，description，以及新的图片
    @POST("/shot")
    suspend fun postShot(@Body shotDetail: ShotDetail): ShotDetail

    //根据storyId获取生成的视频
    @GET("/preview/{storyId}")
    suspend fun getPreview(@Path("storyId") storyId: Int): Response<ResponseBody>
}
