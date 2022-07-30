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

        val SIZE = 31
        var arr = IntArray(SIZE, { 0 } )
        var players: ArrayList<TextView> = ArrayList()
        val kCategory = intent.getStringExtra("kcategory")
        val eCategory = intent.getStringExtra("ecategory")
        category.text = "카테고리 - " + kCategory
        createRetrofit()

        for (i in 0..SIZE) {
            players.add(findViewById(getResources().getIdentifier("board" + i, "id", packageName)))
        }

        next.setOnClickListener {
            val num = playGame()

            for ((index,item) in arr.withIndex()) {
                if (item!=0) {
                    val ind = if (index == 28) 23 else index
                    players[ind].setOnClickListener {
                        var idx = getIndex(ind, num)
                        arr[idx] += item
                        arr[ind] = 0
                        drawGame(arr)
                    }
                }
            }

            start.setOnClickListener {
                if (checkBoard(arr) && num != -1) {
                    arr[num] += 1
                    drawGame(arr)
                }
            }
//            changeQuestion(eCategory!!)
        }
    }

    fun getIndex(index: Int, num: Int): Int {
        var start = index
        var idx: Int

        if (num == -1) {
            if (start == 1)
                return 0
            else if (start == 21)
                return 5
            else if (start == 26)
                return 10
            else
                return index + num
        } else {
            if (start == 5)
                start = 20
            else if (start == 10)
                start = 25
            else if (start == 23)
                start = 28
            else if (start in 16..20) {
                if (start + num > 20)
                    start += 11
            }
            else if (start in 21..25) {
                if (start + num > 25)
                    start -= 11
            }
        }
        idx = if (start + num > 30) 0 else start + num
        return idx
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
            if (index!=0) {
                var player: TextView

                if (index == 28)
                    player = findViewById(getResources().getIdentifier("board23", "id", packageName))
                else
                    player = findViewById(getResources().getIdentifier("board" + index, "id", packageName))

                if (item!=0) {
                    val drawable = resources.getIdentifier("player_$item", "drawable", packageName)
                    player.setBackgroundResource(drawable)
                }
                else
                    player.setBackgroundResource(R.drawable.board)
            }
        }
    }

    fun checkBoard(array: IntArray): Boolean {
        var cnt = 0
        for (item in array)
            cnt += item
        if (cnt == 4)
            return false
        return true
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