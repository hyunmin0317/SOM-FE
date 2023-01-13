package com.smu.som

import android.content.Context
import android.content.Intent
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.android.synthetic.main.activity_game_result.*

class GameResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_result)

        val category = intent.getStringExtra("category")
        val kcategory = intent.getStringExtra("kcategory")
        val name_1p = intent.getStringExtra("name1")
        val name_2p = intent.getStringExtra("name2")
        val result = intent.getStringExtra("result")
        val sp = this.getSharedPreferences("game_sp", Context.MODE_PRIVATE)
        val sound = sp.getBoolean("sound", false)

        if (sound) {
            val soundPool = SoundPool.Builder().build()
            val gamesound = soundPool.load(this, resources.getIdentifier("gameover", "raw", packageName), 1)
            Handler(Looper.getMainLooper()).postDelayed({
                soundPool.play(gamesound, 1.0f, 1.0f, 0, 0, 1.0f)
            }, 500)
        }

        result_text.text = result

        game.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("category", category)
            intent.putExtra("kcategory", kcategory)
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
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}