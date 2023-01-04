package com.smu.som

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_game_data.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GameDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_data)

        var cnt_couple = 0
        var cnt_married = 0
        var cnt_family = 0

        (application as MasterApplication).service.getData(
            "choihm9903@gmail.com"
        ).enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    cnt_couple = data?.couple!!
                    cnt_married = data?.married!!
                    cnt_family = data?.family!!
                    couple_cnt.text = cnt_couple.toString()
                    married_cnt.text = cnt_married.toString()
                    family_cnt.text = cnt_family.toString()
                } else {
                    Log.e(TAG, "불러오기 오류")
                }
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                Log.e(TAG, "서버 오류")
            }
        })

        couple.setOnClickListener {
            if (cnt_couple > 0) {
                startActivity(Intent(this, QuestionListActivity::class.java))
                finish()
            } else {
                Toast.makeText(this@GameDataActivity, "커플 카테고리의 게임 내역이 없습니다.", Toast.LENGTH_LONG).show()
            }
        }

        married.setOnClickListener{
            if (cnt_married > 0) {
                startActivity(Intent(this, QuestionListActivity::class.java))
                finish()
            } else {
                Toast.makeText(this@GameDataActivity, "부부 카테고리의 게임 내역이 없습니다.", Toast.LENGTH_LONG).show()
            }
        }

        family.setOnClickListener {
            if (cnt_family > 0) {
                startActivity(Intent(this, QuestionListActivity::class.java))
                finish()
            } else {
                Toast.makeText(this@GameDataActivity, "부모자녀 카테고리의 게임 내역이 없습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }
}