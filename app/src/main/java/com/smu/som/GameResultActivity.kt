package com.smu.som

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_game_result.*

class GameResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_result)

        val category = intent.getStringExtra("category")
        val name_1p = intent.getStringExtra("name1")
        val name_2p = intent.getStringExtra("name2")
        val result = intent.getStringExtra("result")

        result_text.text = result

        game.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("category", category)
            intent.putExtra("name1", name_1p)
            intent.putExtra("name2", name_2p)
            startActivity(intent)
            finish()
        }

        setting.setOnClickListener {
            val intent = Intent(this, GameSettingActivity::class.java)
            startActivity(intent)
            finish()
        }

        home.setOnClickListener {
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}