package com.smu.som

import android.content.Context
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

        val sp = this.getSharedPreferences("game_sp", Context.MODE_PRIVATE)
        var player1 = sp.getString("name1", "1P")
        var player2 = sp.getString("name2", "2P")
        var category = sp.getInt("category", 0)
        var character1 = sp.getInt("character1", 0)
        var character2 = sp.getInt("character2", 0)

        val characterArray1 = arrayOf("토끼", "병아리")
        val characterArray2 = arrayOf("고양이", "곰")
        val categoryArray = arrayOf("연인", "부부", "부모자녀")
        val categoryMap = hashMapOf("연인" to "COUPLE", "부부" to "MARRIED", "부모자녀" to "PARENT")

        var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categoryArray)
        spinner.adapter = adapter
        spinner.setSelection(category)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, characterArray1)
        spinner1.adapter = adapter
        spinner1.setSelection(character1)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, characterArray2)
        spinner2.adapter = adapter
        spinner2.setSelection(character2)

        name1.setText(player1)
        name2.setText(player2)

        start.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            val kcategory = spinner.getSelectedItem().toString()
            val category = categoryMap[kcategory]
            val editor = sp.edit()
            editor.putString("name1", name1.text.toString())
            editor.putString("name2", name2.text.toString())
            editor.putInt("category", spinner.selectedItemPosition)
            editor.putInt("character1", spinner1.selectedItemPosition)
            editor.putInt("character2", spinner2.selectedItemPosition)
            editor.commit()

            intent.putExtra("category", category)
            intent.putExtra("name1", name1.text.toString())
            intent.putExtra("name2", name2.text.toString())
            intent.putExtra("character1", spinner1.selectedItemPosition)
            intent.putExtra("character2", spinner2.selectedItemPosition)
            startActivity(intent)
            finish()
        }
    }
}