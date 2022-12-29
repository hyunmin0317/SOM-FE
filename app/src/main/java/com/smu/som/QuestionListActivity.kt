package com.smu.som

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_question_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuestionListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_list)

        (application as MasterApplication).service.usedQuestion(
            "choihm9903@gmail.com"
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

        (application as MasterApplication).service.passQuestion(
            "choihm9903@gmail.com"
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