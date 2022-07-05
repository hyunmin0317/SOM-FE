package com.example.som

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.facebook.stetho.okhttp3.StethoInterceptor
import kotlinx.android.synthetic.main.activity_question.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class QuestionActivity : AppCompatActivity() {

    lateinit var service: RetrofitService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        home.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val kCategory = intent.getStringExtra("kcategory")
        val eCategory = intent.getStringExtra("ecategory")
        category.text = "카테고리 - " + kCategory
        content.text = eCategory

        createRetrofit()
        changeQuestion(eCategory!!)

        next.setOnClickListener {
            changeQuestion(eCategory!!)
        }
    }

    fun changeQuestion(category: String) {
        service.getQuestion(
            category
        ).enqueue(object : Callback<Question> {
            override fun onResponse(call: Call<Question>, response: Response<Question>) {
                if (response.isSuccessful) {
                    val question = response.body()
                    content.text = question!!.content
                } else {
                    Toast.makeText(this@QuestionActivity, "잘못된 카테고리 입니다.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Question>, t: Throwable) {
                Toast.makeText(this@QuestionActivity, "서버 오류", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun createRetrofit() {
        val header = Interceptor {
            val original = it.request()
            it.proceed(original)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(header)
            .addNetworkInterceptor(StethoInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        service = retrofit.create(RetrofitService::class.java)
    }
}