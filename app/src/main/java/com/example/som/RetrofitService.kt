package com.example.som

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitService {
    @GET("question/{category}/")
    fun getQuestion(
        @Path("category") category: String
    ): Call<Question>
}