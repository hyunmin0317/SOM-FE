package com.smu.som

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_question_list.*

class QuestionListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_list)

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