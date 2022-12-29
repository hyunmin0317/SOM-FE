package com.smu.som

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.AgeRange
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        UserApiClient.instance.me { user, error ->
            val ageRange = user?.kakaoAccount?.ageRange
            val email = user?.kakaoAccount?.email
            var adult = false


            if (ageRange == AgeRange.AGE_20_29 || ageRange == AgeRange.AGE_30_39 || ageRange == AgeRange.AGE_40_49 || ageRange == AgeRange.AGE_50_59
                || ageRange == AgeRange.AGE_60_69 || ageRange == AgeRange.AGE_70_79 || ageRange == AgeRange.AGE_80_89 || ageRange == AgeRange.AGE_90_ABOVE) {
                adult = true
            }

            val sp = this.getSharedPreferences("game_sp", Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putBoolean("adult", adult)
            editor.putString("email", email)
            editor.commit()
        }


        start.setOnClickListener {
            startActivity(Intent(this, GameSettingActivity::class.java))
            finish()
        }

        mypage.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}