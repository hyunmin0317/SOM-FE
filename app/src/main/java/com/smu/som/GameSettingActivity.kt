package com.smu.som

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_game_setting.*
import kotlinx.android.synthetic.main.activity_game_setting.start
import kotlinx.android.synthetic.main.activity_main.*


class GameSettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_setting)

        val categoryArray = arrayOf("연인", "부부", "부모자녀") // 리스트에 들어갈 Array
        val categoryMap = hashMapOf("연인" to "COUPLE", "부부" to "MARRIED", "부모자녀" to "PARENT")

        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categoryArray)
        spinner.adapter = adapter

        start.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            val kcategory = spinner.getSelectedItem().toString()
            val category = categoryMap[kcategory]
            intent.putExtra("category", category)
            intent.putExtra("name1", name1.text.toString())
            intent.putExtra("name2", name2.text.toString())
            startActivity(intent)
            finish()
        }

    //        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                val kcategory = categoryArray.get(position)
//                val category = categoryMap[kcategory]
//                intent.putExtra("category", category)
//                startActivity(intent)
//                finish()
//            }
//            override fun onNothingSelected(parent: AdapterView<*>?) {
////                intent.putExtra("category", categoryMap["연인"])
////                startActivity(intent)
////                finish()
//            }
//        }
    }
}