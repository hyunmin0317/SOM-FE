package com.smu.som

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.AgeRange
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val categoryArray = arrayOf("연인", "부부", "부모자녀") // 리스트에 들어갈 Array
        val categoryMap = hashMapOf("연인" to "COUPLE", "부부" to "MARRIED", "부모자녀" to "PARENT")


        UserApiClient.instance.me { user, error ->
            val ageRange = user?.kakaoAccount?.ageRange
            var isAdult = "n"


            if (ageRange == AgeRange.AGE_20_29 || ageRange == AgeRange.AGE_30_39 || ageRange == AgeRange.AGE_40_49 || ageRange == AgeRange.AGE_50_59
                || ageRange == AgeRange.AGE_60_69 || ageRange == AgeRange.AGE_70_79 || ageRange == AgeRange.AGE_80_89 || ageRange == AgeRange.AGE_90_ABOVE) {
                isAdult = "y"
            }

            val sp = this.getSharedPreferences("login_sp", Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putString("isAdult", isAdult)
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