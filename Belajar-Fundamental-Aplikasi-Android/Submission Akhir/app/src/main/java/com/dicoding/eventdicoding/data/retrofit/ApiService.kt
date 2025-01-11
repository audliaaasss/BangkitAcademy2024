package com.dicoding.eventdicoding.data.retrofit
import com.dicoding.eventdicoding.data.response.DetailEventResponse
import com.dicoding.eventdicoding.data.response.EventResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("events")
    fun getActiveEvents(
        @Query("active") active: Int = 1
    ): Call<EventResponse>

    @GET("events")
    fun getFinishedEvents(
        @Query("active") active: Int = 0
    ): Call<EventResponse>

    @GET("events/{id}")
    fun getDetailEvent(
        @Path("id") id: String
    ): Call<DetailEventResponse>
}