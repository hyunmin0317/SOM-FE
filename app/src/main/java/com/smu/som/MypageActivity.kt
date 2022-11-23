package com.smu.som

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_mypage.*

class MypageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        val sp = this.getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val editor = sp.edit()

        UserApiClient.instance.me { user, error ->
            val nickname = user?.kakaoAccount?.profile?.nickname
            val profileImageUrl = user?.kakaoAccount?.profile?.profileImageUrl

            name.text = nickname.toString()
            if (profileImageUrl == null)
                setImage("https://github.com/hyunmin0317/Outstagram/blob/master/github/basic.jpg?raw=true")
            else
                setImage(profileImageUrl)

            val sp = this.getSharedPreferences("login_sp", Context.MODE_PRIVATE)
            val isAdult = sp.getString("isAdult", "n")
            if (isAdult == "y") {
                age.text = "성인 O"
            } else {
                age.text = "성인 X"
            }
        }

        logout.setOnClickListener {
            editor.putBoolean("isAdult", false)
            editor.commit()
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                }
                else {
                    Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                    startActivity(Intent(this, IntroActivity::class.java))
                    finish()
                }
            }
        }

        unlink.setOnClickListener {
            editor.putBoolean("isAdult", false)
            editor.commit()
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Log.e(TAG, "탈퇴 실패. SDK에서 토큰 삭제됨", error)
                }
                else {
                    Log.i(TAG, "탈퇴 성공. SDK에서 토큰 삭제됨")
                    startActivity(Intent(this, IntroActivity::class.java))
                    finish()
                }
            }
        }

        home.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun setImage(url: String?) {
        Glide.with(this).load(url).into(findViewById(R.id.profile_img))
    }
}