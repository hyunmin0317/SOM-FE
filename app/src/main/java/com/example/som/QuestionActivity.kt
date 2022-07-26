package com.example.som

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

        var arr = IntArray(20, { 0 } )
        var cnt = 4
        val kCategory = intent.getStringExtra("kcategory")
        val eCategory = intent.getStringExtra("ecategory")
        category.text = "카테고리 - " + kCategory

        createRetrofit()


        next.setOnClickListener {
            val num = playGame()

//            if (checkBoard(arr)) {
//                for ((index, item) in arr.withIndex()) {
//                    if (item!=0) {
//                        val idx = (index+num) % 20
//                        arr[idx] = item
//                        arr[index] = 0
//                        break
//                    }
//                }
//            } else {
//                if (num != -1)
//                    arr[num] += 1
//            }

            start.setOnClickListener {
                if (cnt != 0 && num != -1) {
                    arr[num] += 1
                    drawGame(arr)
                    cnt--
                }
            }

//            changeQuestion(eCategory!!)
        }
    }

    fun playGame(): Int {
        val yuts = arrayOf("백도", "도", "개", "걸", "윷", "모")
        val range = (0..5)
        var num = range.random()
        val drawable = resources.getIdentifier("yut_$num", "drawable", packageName)
        yut.setBackgroundResource(drawable)
        yut.setText(yuts[num])
        if (num == 0)
            num = -1
        return num
    }

    fun drawGame(array: IntArray) {
        for ((index,item) in array.withIndex()) {
            val player: TextView = findViewById(getResources().getIdentifier("board" + index, "id", packageName))

            if (item!=0) {
                val drawable = resources.getIdentifier("player_$item", "drawable", packageName)
                player.setBackgroundResource(drawable)
            }
            else
                player.setBackgroundResource(R.drawable.board)
        }
    }

    fun checkBoard(array: IntArray): Boolean {
        for (item in array) {
            if (item!=0)
                return true
        }
        return false
    }

    fun changeQuestion(category: String) {
        val builder = AlertDialog.Builder(this)

        service.getQuestion(
            category
        ).enqueue(object : Callback<Question> {
            override fun onResponse(call: Call<Question>, response: Response<Question>) {
                if (response.isSuccessful) {
                    val question = response.body()
                    builder.setTitle("질문").setMessage(question!!.content)
                        .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id -> })
                    builder.show()
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