package com.smu.som

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.Gender
import kotlinx.android.synthetic.main.activity_mypage.*

class MypageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        val sp = this.getSharedPreferences("game_sp", Context.MODE_PRIVATE)
        val editor = sp.edit()
        val adult = sp.getBoolean("adult", false)
        val sound = sp.getBoolean("sound", false)
        val isAdult = sp.getString("isAdult", "n")

        UserApiClient.instance.me { user, error ->
            val nickname = user?.kakaoAccount?.profile?.nickname
            val genders = user?.kakaoAccount?.gender
            val profileImageUrl = user?.kakaoAccount?.profile?.profileImageUrl
            var ageRange = user?.kakaoAccount?.ageRange

            name.text = nickname.toString()
            age_range.text = changeFormat(ageRange.toString())
            if (genders == Gender.MALE)
                gender.text = "남"
            else if (genders == Gender.FEMALE)
                gender.text = "여"

            if (profileImageUrl == null)
                setImage("https://github.com/hyunmin0317/Outstagram/blob/master/github/basic.jpg?raw=true")
            else
                setImage(profileImageUrl)
        }

        if (adult) {
            if (isAdult == "y") {
                age.isChecked = true
                age.text = "성인 질문 ON"
            }
            age.isEnabled = true
            age.setOnCheckedChangeListener { p0, isChecked ->
                if (isChecked) {
                    editor.putString("isAdult", "y")
                    editor.commit()
                    age.text = "성인 질문 ON"
                } else {
                    editor.putString("isAdult", "n")
                    editor.commit()
                    age.text = "성인 질문 OFF"
                }
            }
        } else {
            editor.putString("isAdult", "n")
            editor.commit()
            age.isEnabled = false
            age.text = "성인 질문 OFF"
        }

        if (sound) {
            Sound.isChecked = true
            Sound.text = "소리 ON"
        }
        Sound.setOnCheckedChangeListener { p0, isChecked ->
            if (isChecked) {
                editor.putBoolean("sound", true)
                editor.commit()
                Sound.text = "소리 ON"
            } else {
                editor.putBoolean("sound", false)
                editor.commit()
                Sound.text = "소리 OFF"
            }
        }

        logout.setOnClickListener {
            editor.putBoolean("adult", false)
            editor.putString("isAdult", "n")
            editor.putString("name1", "1P")
            editor.putString("name2", "2P")
            editor.putInt("category", 0)
            editor.putString("email", null)
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
            editor.putBoolean("adult", false)
            editor.putString("isAdult", "n")
            editor.putString("name1", "1P")
            editor.putString("name2", "2P")
            editor.putInt("category", 0)
            editor.putString("email", null)
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
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        question.setOnClickListener{
            startActivity(Intent(this, GameDataActivity::class.java))
            finish()
        }
    }

    fun setImage(url: String?) {
        Glide.with(this).load(url).into(findViewById(R.id.profile_img))
    }

    fun changeFormat(ageRange: String?): String {
        val string = ageRange?.split("_")
        if (string?.size!! > 1) {
            return string[1] + "~" + string[2]
        }
        return ageRange
    }
}