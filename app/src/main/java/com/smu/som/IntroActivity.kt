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
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            else if (tokenInfo != null) {
                val intent = Intent(this, MypageActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

//        if ((application as MasterApplication).checkIsLogin()) {
//            handler.postDelayed({
//                var intent = Intent(this, LoginActivity::class.java)
//                startActivity(intent)
//            }, 3000)
//        } else {
//            handler.postDelayed({
//                var intent = Intent(this, LoginActivity::class.java)
//                startActivity(intent)
//            }, 3000)
//        }

    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}