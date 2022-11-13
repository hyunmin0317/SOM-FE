package com.smu.som

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*




class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        temp_Btn.setOnClickListener{

            // 가입하기 버튼을 누르면 들어오는 로직
            // 회원가입(JoinActivity) 화면으로 이동하게 Intent 사용
            val myIntent = Intent(this, temp::class.java)

            // startActivity를 해야 화면이동
            startActivity(myIntent)
        }

        val categoryArray = arrayOf("연인", "부부", "부모자녀") // 리스트에 들어갈 Array
        val categoryMap = hashMapOf("연인" to "married", "부부" to "married", "부모자녀" to "married")

        start.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("카테고리 선택하기")
                .setItems(categoryArray,
                    DialogInterface.OnClickListener { dialog, which ->
                        val intent = Intent(this, QuestionActivity::class.java)
                        val kcategory = categoryArray[which]
                        val ecategory = categoryMap[kcategory]
                        intent.putExtra("kcategory", kcategory)
                        intent.putExtra("ecategory", ecategory)
                        startActivity(intent)
                    })
            builder.show()
        }
    }
}