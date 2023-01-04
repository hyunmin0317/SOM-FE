package com.smu.som

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.Gender
import kotlinx.android.synthetic.main.activity_mypage.*
import kotlinx.android.synthetic.main.activity_question_list.*
import kotlinx.android.synthetic.main.activity_question_list.home
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuestionListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_list)

        val sp = this.getSharedPreferences("game_sp", Context.MODE_PRIVATE)
        val email = sp.getString("email", null)
        var category = intent.getStringExtra("category")

        email?.let {
            (application as MasterApplication).service.usedQuestion(
                it, category!!
            ).enqueue(object : Callback<ArrayList<Question>> {
                override fun onResponse(call: Call<ArrayList<Question>>, response: Response<ArrayList<Question>>) {
                    if (response.isSuccessful) {
                        val questionList = response.body()
                        val adapter = QuestionAdapter(
                            questionList!!,
                            LayoutInflater.from(this@QuestionListActivity)
                        )
                        used_recyclerview.adapter = adapter
                        used_recyclerview.layoutManager = LinearLayoutManager(this@QuestionListActivity)
                    } else {
                        Log.e(ContentValues.TAG, "잘못된 카테고리 입니다.")
                    }
                }

                override fun onFailure(call: Call<ArrayList<Question>>, t: Throwable) {
                    Log.e(ContentValues.TAG, "서버 오류")
                }
            })
        }

        email?.let {
            (application as MasterApplication).service.passQuestion(
                it, category!!
            ).enqueue(object : Callback<ArrayList<Question>> {
                override fun onResponse(call: Call<ArrayList<Question>>, response: Response<ArrayList<Question>>) {
                    if (response.isSuccessful) {
                        val questionList = response.body()
                        val adapter = QuestionAdapter(
                            questionList!!,
                            LayoutInflater.from(this@QuestionListActivity)
                        )
                        pass_recyclerview.adapter = adapter
                        pass_recyclerview.layoutManager = LinearLayoutManager(this@QuestionListActivity)
                    } else {
                        Log.e(ContentValues.TAG, "잘못된 카테고리 입니다.")
                    }
                }

                override fun onFailure(call: Call<ArrayList<Question>>, t: Throwable) {
                    Log.e(ContentValues.TAG, "서버 오류")
                }
            })
        }

        home.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        back.setOnClickListener {
            startActivity(Intent(this, MypageActivity::class.java))
            finish()
        }
    }
}