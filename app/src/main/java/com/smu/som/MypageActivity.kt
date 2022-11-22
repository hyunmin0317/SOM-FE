package com.smu.som

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.AgeRange
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_mypage.*

class MypageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        UserApiClient.instance.me { user, error ->
            val nickname = user?.kakaoAccount?.profile?.nickname
            val ageRange = user?.kakaoAccount?.ageRange

            Log.i(TAG, user?.kakaoAccount?.profile?.nickname.toString())
            Log.i(TAG, user?.kakaoAccount?.profile?.profileImageUrl.toString())

            if (ageRange == AgeRange.AGE_20_29 || ageRange == AgeRange.AGE_30_39 || ageRange == AgeRange.AGE_40_49 || ageRange == AgeRange.AGE_50_59
                || ageRange == AgeRange.AGE_60_69 || ageRange == AgeRange.AGE_70_79 || ageRange == AgeRange.AGE_80_89 || ageRange == AgeRange.AGE_90_ABOVE) {
                Log.e(TAG, "성인: ${ageRange}")
            }
            age.text = ageRange.toString()
            name.text = nickname.toString()
        }

        logout.setOnClickListener {
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
}