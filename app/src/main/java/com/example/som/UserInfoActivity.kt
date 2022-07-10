package com.example.som

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_user_info.*


class UserInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        var username = getUserName()
        if (username == null)
            username = ""
        user.text = username

        all_list.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
        my_list.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
        upload.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
        user_info.setOnClickListener { startActivity(Intent(this, UserInfoActivity::class.java)) }

        logout.setOnClickListener {
            val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putString("username", "null")
            editor.putString("token", "null")
            editor.commit()
            (application as MasterApplication).createRetrofit()
            finish()
            startActivity(Intent(this, IntroActivity::class.java))
        }
    }

    fun getUserName(): String? {
        val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val username = sp.getString("username", "null")
        if (username == "null") return null
        else return username
    }
}
