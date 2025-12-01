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
    @POST("story")
    suspend fun postStory(@Body createStoryRequest: CreateStoryRequest): Int

    // 获取所有的story
    @GET("story")
    suspend fun getStories(): List<Story>

    // 获取一个story下的所有shots
    //@GET("story/{storyId}/shots")
    //suspend fun getShots(@Path("storyId") storyId: Int): List<Shot>

    @GET("story/{storyId}/shots")
    // 核心修改：返回类型改为 ShotResponse
    suspend fun getShots(@Path("storyId") storyId: Int): ShotResponse

    // 通过storyId获取一个story相关的内容
    @GET("story/{storyId}")
    suspend fun getStory(@Path("storyId") storyId: Int): Story

    // 通过storyId删除一个story
    @DELETE("story/{storyId}")
    suspend fun deleteStory(@Path("storyId") storyId: Int): Response<ResponseBody>

    // 通过shotId获取一个shot的详细内容
    @GET("shot/{shotId}")
    suspend fun getShot(@Path("shotId") shotId: Int): Shot

    // 更改shot的详细内容，并返回更新后的内容， 包括 narration，transition，description，以及新的图片
    @POST("shot")
    suspend fun postShot(@Body shot: Shot): Shot

    //根据storyId获取生成的视频url
    @GET("preview/{storyId}")
    suspend fun getPreview(@Path("storyId") storyId: Int): String
}
