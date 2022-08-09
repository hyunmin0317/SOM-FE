package com.example.som

import android.content.DialogInterface
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_question.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Math.abs

class QuestionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        val SIZE = 30
        var arr = IntArray(SIZE, { 0 } )
        var players: ArrayList<TextView> = ArrayList()
        val kCategory = intent.getStringExtra("kcategory")
        val eCategory = intent.getStringExtra("ecategory")
        var turn = true

        category.text = "카테고리 - " + kCategory

        for (i in 0..SIZE) {
            players.add(findViewById(getResources().getIdentifier("board" + i, "id", packageName)))
        }

        next.setOnClickListener {
            val num = playGame()

            for ((index,item) in arr.withIndex()) {
                if (item!=0 && index!=0) {
                    players[index].setOnClickListener {
                        var idx = getIndex(index, num)

                        if (turn == item > 0) {
                            if (turn == arr[idx] < 0)
                                arr[idx] = item
                            else
                                arr[idx] += item
                            arr[index] = 0
                            drawGame(arr)
                            turn = !turn

                            start.setOnClickListener(null)
                            for ((index,item) in arr.withIndex())
                                if (item!=0 && index!=0)
                                    players[index]?.setOnClickListener(null)
                        }
                    }
                }
            }

            start.setOnClickListener {
                if (num != -1 && checkBoard(arr, turn)) {
                    if (turn) {
                        if (arr[num] < 0)
                            arr[num] = 1
                        else
                            arr[num] += 1
                    }
                    else {
                        if (arr[num] > 0)
                            arr[num] = -1
                        else
                            arr[num] -= 1
                    }
                    drawGame(arr)
                    turn = !turn

                    start.setOnClickListener(null)
                    for ((index,item) in arr.withIndex())
                        if (item!=0 && index!=0)
                            players[index]?.setOnClickListener(null)
                }
            }
//            changeQuestion(eCategory!!)
        }
    }

    fun getIndex(index: Int, num: Int): Int {
        var idx = index

        if (num == -1) {
            if (idx == 1)
                return 20
            else if (idx == 21)
                return 5
            else if (idx == 26)
                return 10
            else if (idx == 28)
                return 23
            else
                return index + num
        } else {
            if (idx == 5)
                idx = 20
            else if (idx == 10) {
                if (num in 1..2)
                    idx = 25
                else if (num == 3)
                    return 23
                else
                    idx = 24
            }
            else if (idx == 23)
                idx = 27
            else if (idx in 16..20) {
                if (idx + num > 20)
                    return 0
            }
            else if (idx in 21..25) {
                if (idx + num > 25)
                    idx -= 11
            }
            else if (idx in 26..27) {
                if (idx + num == 28)
                    return 23
                if (idx + num > 28)
                    idx--
            }
        }
        if (idx+num == 30)
            return 20
        else if (idx+num > 30)
            return 0
        else
            return idx + num
    }

    fun playGame(): Int {
        val yuts = arrayOf("백도", "도", "개", "걸", "윷", "모")
        val range = (0..5)
        var num = range.random()
        val drawable = resources.getIdentifier("yut_$num", "drawable", packageName)
        yut.setBackgroundResource(drawable)
        yut.setText(yuts[num])
        if (num == 0)
            num = -1
        return num
    }

    fun drawGame(array: IntArray) {
        for ((index,item) in array.withIndex()) {
            if (index!=0) {
                var player: TextView = findViewById(getResources().getIdentifier("board" + index, "id", packageName))

                if (item!=0) {
                    var drawable: Int
                    if (item > 0)
                        drawable = resources.getIdentifier("player_$item", "drawable", packageName)
                    else
                        drawable = resources.getIdentifier(String.format("player_%02d", abs(item)),"drawable", packageName)
                    player.setBackgroundResource(drawable)
                }
                else
                    player.setBackgroundResource(R.drawable.board)
            }
        }
    }

    fun checkBoard(array: IntArray, turn: Boolean): Boolean {
        var cnt = 0
        for (item in array) {
            if (turn && item > 0 || !turn && item < 0)
                cnt += item
        }
        if (cnt >= 4 || cnt <= -4)
            return false
        return true
    }

    fun changeQuestion(category: String) {
        val builder = AlertDialog.Builder(this)

        (application as MasterApplication).service.getQuestion(
            category
        ).enqueue(object : Callback<Question> {
            override fun onResponse(call: Call<Question>, response: Response<Question>) {
                if (response.isSuccessful) {
                    val question = response.body()
                    builder.setTitle("질문").setMessage(question!!.content)
                        .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id -> })
                    builder.show()
                } else {
                    Toast.makeText(this@QuestionActivity, "잘못된 카테고리 입니다.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Question>, t: Throwable) {
                Toast.makeText(this@QuestionActivity, "서버 오류", Toast.LENGTH_LONG).show()
            }
        })
    }
}