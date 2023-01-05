package com.smu.som

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    @GET("api/question/{category}/")
    fun getQuestion(
        @Path("category") category: String,
        @Query("isAdult") isAdult: String
    ): Call<ArrayList<Question>>

    @GET("api/question/{kakaoID}/{category}/used")
    fun usedQuestion(
        @Path("kakaoID") kakaoID: String,
        @Path("category") category: String
    ): Call<ArrayList<Question>>


    @GET("api/question/{kakaoID}/{category}/pass")
    fun passQuestion(
        @Path("kakaoID") kakaoID: String,
        @Path("category") category: String
    ): Call<ArrayList<Question>>

    @POST("api/question/{kakaoID}/{category}")
    fun saveResult(
        @Path("kakaoID") kakaoID: String,
        @Path("category") category: String,
        @Body result: GameResult
    ): Call<Boolean>

    @GET("api/question/playcount/{kakaoID}")
    fun getData(
        @Path("kakaoID") kakaoID: String,
    ): Call<Data>
}