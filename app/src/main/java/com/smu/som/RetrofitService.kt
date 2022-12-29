package com.smu.som

import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    @GET("api/question/{category}/")
    fun getQuestion(
        @Path("category") category: String,
        @Query("isAdult") isAdult: String
    ): Call<ArrayList<Question>>
}