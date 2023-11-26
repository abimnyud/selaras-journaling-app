package com.enjoy.selaras.interfaces

import com.enjoy.selaras.entities.Emotion
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface EmotionApi {
    @GET("/classify")
    suspend fun getEmotion(@Query("text") text: String) : Response<Emotion>
}