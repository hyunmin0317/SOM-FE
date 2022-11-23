package com.smu.som

import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    @GET("question/{category}/")
    fun getQuestion(
        @Path("category") category: String
    ): Call<Question>
}