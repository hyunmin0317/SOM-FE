package com.smu.som

import android.content.DialogInterface
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
        var player1 = 4
        var player2 = 4
        var score1 = 0
        var score2 = 0
        var turn = true

        val builder = AlertDialog.Builder(this)
        val kCategory = intent.getStringExtra("kcategory")
        val eCategory = intent.getStringExtra("ecategory")

        for (i in 0..SIZE) {
            players.add(findViewById(getResources().getIdentifier("board" + i, "id", packageName)))
        }

        category.text = kCategory
        drawGame(arr, player1, player2, score1, score2, turn)

        yut.setOnClickListener {
            yut.isClickable = false
            val num = playGame()

            for ((index,item) in arr.withIndex()) {
                if (item!=0 && index!=0) {
                    players[index].setOnClickListener {
                        if (turn == item > 0) {
                            var idx = getIndex(index, num)

                            if (turn) {
                                if (arr[idx] < 0 && idx != 0) {     // 말을 잡을 경우
                                    player2 -= arr[idx]
                                    arr[idx] = item

                                    builder.setTitle("말을 잡았습니다!").setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id -> })
                                    builder.show()
                                }
                                else {
                                    if (idx == 0)
                                        score1 += item
                                    else
                                        arr[idx] += item
                                    turn = !turn
                                }
                            }
                            else {
                                if (arr[idx] > 0 && idx != 0) {     // 말을 잡을 경우
                                    player1 += arr[idx]
                                    arr[idx] = item

                                    builder.setTitle("말을 잡았습니다!").setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id -> })
                                    builder.show()
                                }
                                else {
                                    if (idx == 0)
                                        score2 -= item
                                    else
                                        arr[idx] += item
                                    turn = !turn
                                }
                            }
                            arr[index] = 0
                            drawGame(arr, player1, player2, score1, score2, turn)

                            start.setOnClickListener(null)
                            for ((index,item) in arr.withIndex())
                                if (item!=0 && index!=0)
                                    players[index]?.setOnClickListener(null)
                            yut.isClickable = true
                        }
                    }
                }
            }

            start.setOnClickListener {
                if (num != -1 && checkBoard(turn, player1, player2) != 0) {
                    if (turn) {
                        if (arr[num] < 0) {     // 말을 잡을 경우
                            player2 -= arr[num]
                            arr[num] = 1

                            builder.setTitle("말을 잡았습니다!").setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id -> })
                            builder.show()
                        }
                        else {
                            arr[num] += 1
                            turn = !turn
                        }
                        player1 -= 1
                    }
                    else {
                        if (arr[num] > 0) {     // 말을 잡을 경우
                            player1 += arr[num]
                            arr[num] = -1

                            builder.setTitle("말을 잡았습니다!").setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id -> })
                            builder.show()
                        }
                        else {
                            arr[num] -= 1
                            turn = !turn
                        }
                        player2 -= 1
                    }
                    drawGame(arr, player1, player2, score1, score2, turn)

                    start.setOnClickListener(null)
                    for ((index,item) in arr.withIndex())
                        if (item!=0 && index!=0)
                            players[index]?.setOnClickListener(null)
                    yut.isClickable = true
                }
            }

            if (num == -1 && checkBoard(turn, player1, player2) == 4) {
                yut.isClickable = true
                builder.setTitle("한 번 더 던지세요!").setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id -> })
                builder.show()
            }
            else {
                changeQuestion(eCategory!!)
            }
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
        var num = percentage()
        val drawable = resources.getIdentifier("yut_$num", "drawable", packageName)
        yut.setBackgroundResource(drawable)
        yut.setText(yuts[num])
        if (num == 0)
            num = -1
        return num
    }

    fun percentage(): Int {
        val per = arrayOf(2, 17, 31, 34, 13, 3)
        val range = (1..100)
        var num = range.random()

        for ((index,item) in per.withIndex()) {
            if (num <= item)
                return index
            num -= item
        }
        return 5
    }

    fun drawGame(array: IntArray, player01: Int, player02: Int, score01: Int, score02: Int, turn: Boolean) {
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

        for (num in 1..4) {
            var player1: TextView = findViewById(getResources().getIdentifier("player1_" + num, "id", packageName))
            var player2: TextView = findViewById(getResources().getIdentifier("player2_" + num, "id", packageName))

            if (num <= player01)
                player1.setBackgroundResource(R.drawable.player_1)
            else
                player1.setBackgroundResource(R.drawable.noplayer)

            if (num <= player02)
                player2.setBackgroundResource(R.drawable.player_01)
            else
                player2.setBackgroundResource(R.drawable.noplayer)
        }

        score1.text = score01.toString()
        score2.text = score02.toString()

        showTurn(turn)
    }

    fun checkBoard(turn: Boolean, player01: Int, player02: Int): Int {
        if (turn) {
            return player01
        }
        return player02
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

    fun showTurn(turn: Boolean) {
        if (turn) {
            player1.setBackgroundResource(R.drawable.check_box)
            player2.setBackgroundResource(R.drawable.white_box)
        } else {
            player1.setBackgroundResource(R.drawable.white_box)
            player2.setBackgroundResource(R.drawable.check_box)
        }
    }
}