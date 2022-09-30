package com.smu.som

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)


        var handler = Handler()
        if ((application as MasterApplication).checkIsLogin()) {
            handler.postDelayed({
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }, 3000)
        } else {
            handler.postDelayed({
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }, 3000)
        }

    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}