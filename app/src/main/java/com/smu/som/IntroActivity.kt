package com.smu.som

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.kakao.sdk.user.UserApiClient

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)


        var handler = Handler()

        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                handler.postDelayed({
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 3000)
            }
            else if (tokenInfo != null) {
                handler.postDelayed({
                    val intent = Intent(this, StartActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 3000)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}