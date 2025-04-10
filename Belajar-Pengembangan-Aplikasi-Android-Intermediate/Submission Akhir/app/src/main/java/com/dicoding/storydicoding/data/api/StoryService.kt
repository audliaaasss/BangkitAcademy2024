package com.dicoding.storydicoding.data.api

import com.dicoding.storydicoding.data.response.AddStoryResponse
import com.dicoding.storydicoding.data.response.DetailResponse
import com.dicoding.storydicoding.data.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface StoryService {
    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): StoryResponse

    @GET("stories/{id}")
    suspend fun getStoryDetail(
        @Path("id") storyId: String
    ): DetailResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStories(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): AddStoryResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location : Int = 1
    ): StoryResponse
}