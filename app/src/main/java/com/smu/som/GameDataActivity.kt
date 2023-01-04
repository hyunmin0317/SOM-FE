package com.smu.som

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_game_data.*

class GameDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_data)

        couple.setOnClickListener {
            startActivity(Intent(this, QuestionListActivity::class.java))
            finish()
        }

        married.setOnClickListener{
            startActivity(Intent(this, QuestionListActivity::class.java))
            finish()
        }

        family.setOnClickListener {
            startActivity(Intent(this, QuestionListActivity::class.java))
            finish()
        }
    }
}