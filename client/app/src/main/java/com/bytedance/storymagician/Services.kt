package com.bytedance.storymagician

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AppService {
    // 生成一个新的story
    @POST("stories/create/")
    suspend fun createStory(@Body createStoryRequest: CreateStoryRequest): JsonObject

    // 获取所有的story
    @GET("stories/storyList/")
    suspend fun getStories(): List<Story>

    @GET("stories/sceneList/")
    suspend fun getShots(@Query("id") storyId: Int): List<Shot>

    // 通过storyId获取一个story相关的内容
    @GET("story/{storyId}/")
    suspend fun getStory(@Path("storyId") storyId: Int): Story

    // 通过storyId删除一个story
    @DELETE("story/{storyId}/")
    suspend fun deleteStory(@Path("storyId") storyId: Int): Response<ResponseBody>

    // 通过shotId获取一个shot的详细内容
    @GET("scenes/detail/{id}/")
    suspend fun getShot(@Path("id") shotId: Int): Shot

    // 更改shot的详细内容，并返回更新后的内容， 包括 narration，transition，description，以及新的图片
    @POST("scenes/generate/")
    suspend fun postShot(@Body regenerateShotRequest: RegenerateShotRequest): Response<ResponseBody>

    //根据storyId获取生成的视频url
    @GET("preview/{storyId}/")
    suspend fun getPreview(@Path("storyId") storyId: Int): String
}
