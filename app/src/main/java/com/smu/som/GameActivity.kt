package com.smu.som

import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_game.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Math.abs

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val sp = this.getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val isAdult = sp.getString("isAdult", "n")
        val categoryArray = arrayOf("홈으로", "다시하기") // 리스트에 들어갈 Array

        val SIZE = 30
        var arr = IntArray(SIZE, { 0 } )
        var players: ArrayList<TextView> = ArrayList()
        var player1 = 4
        var player2 = 4
        var score1 = 0
        var score2 = 0
        var turn = true

        val builder = AlertDialog.Builder(this)
        val category = intent.getStringExtra("category")
        val name_1p = intent.getStringExtra("name1")
        val name_2p = intent.getStringExtra("name2")

        val range = (1..2)
        val rand1 = range.random()
        val rand2 = range.random()

        name1.text = name_1p
        name2.text = name_2p

        for (i in 0..SIZE) {
            players.add(findViewById(getResources().getIdentifier("board" + i, "id", packageName)))
        }

        stop.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("일시정지").setItems(categoryArray, DialogInterface.OnClickListener { dialog, which ->
                        if (which == 0) {
                            val intent = Intent(this, IntroActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(this, GameActivity::class.java)
                            intent.putExtra("category", category)
                            startActivity(intent)
                            finish()
                        }
                    }).setNegativeButton("취소", DialogInterface.OnClickListener { dialog, id -> })
            builder.show()
        }

        game_rule.setOnClickListener { showPopup() }

        drawGame(arr, player1, player2, score1, score2, turn, rand1, rand2, category, name_1p, name_2p)

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
                                    builder.setTitle("말을 잡았습니다!").setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id -> }).show()
                                }
                                else {
                                    if (idx == 0)
                                        score1 += item
                                    else
                                        arr[idx] += item
                                    if (num != 4 && num!= 5)
                                        turn = !turn
                                    else
                                        builder.setTitle("한 번 더!").setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id -> }).show()
                                }
                            }
                            else {
                                if (arr[idx] > 0 && idx != 0) {     // 말을 잡을 경우
                                    player1 += arr[idx]
                                    arr[idx] = item
                                    builder.setTitle("말을 잡았습니다!").setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id -> }).show()
                                }
                                else {
                                    if (idx == 0)
                                        score2 -= item
                                    else
                                        arr[idx] += item
                                    if (num != 4 && num!= 5)
                                        turn = !turn
                                }
                            }
                            arr[index] = 0
                            drawGame(arr, player1, player2, score1, score2, turn, rand1, rand2, category, name_1p, name_2p)

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
                            builder.setTitle("말을 잡았습니다!").setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id -> }).show()
                        }
                        else {
                            arr[num] += 1
                            if (num != 4 && num!= 5)
                                turn = !turn
                            else
                                builder.setTitle("한 번 더!").setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id -> }).show()
                        }
                        player1 -= 1
                    }
                    else {
                        if (arr[num] > 0) {     // 말을 잡을 경우
                            player1 += arr[num]
                            arr[num] = -1
                            builder.setTitle("말을 잡았습니다!").setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id -> }).show()
                        }
                        else {
                            arr[num] -= 1
                            if (num != 4 && num!= 5)
                                turn = !turn
                            else
                                builder.setTitle("한 번 더!").setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id -> }).show()
                        }
                        player2 -= 1
                    }
                    drawGame(arr, player1, player2, score1, score2, turn, rand1, rand2, category, name_1p, name_2p)

                    start.setOnClickListener(null)
                    for ((index,item) in arr.withIndex())
                        if (item!=0 && index!=0)
                            players[index]?.setOnClickListener(null)
                    yut.isClickable = true
                }
            }

            if (num == -1 && checkBoard(turn, player1, player2) == 4) {
                yut.isClickable = true
                builder.setTitle("한 번 더 던지세요!").setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id -> }).show()
            }
            else {
                if (num != 4 && num!= 5)
                    changeQuestion(category!!, isAdult!!, num)
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
        var value = num

        result.setBackgroundResource(resources.getIdentifier("result_$value", "drawable", packageName))
        Handler(Looper.getMainLooper()).postDelayed({
            result_text.setText(yuts[value])
            result.setBackgroundResource(resources.getIdentifier("yut_$value", "drawable", packageName))
        }, 1500)
        if (num == 0)
            num = -1
        return num
    }

    fun percentage(): Int {
        val per = arrayOf(625, 1875, 3750, 2500, 625, 625)
        val range = (1..10000)
        var num = range.random()

        for ((index,item) in per.withIndex()) {
            if (num <= item)
                return index
            num -= item
        }
        return 5
    }

    fun drawGame(array: IntArray, player01: Int, player02: Int, score01: Int, score02: Int, turn: Boolean, rand1: Int, rand2: Int, category: String?, name1: String?, name2: String?) {
        for ((index,item) in array.withIndex()) {
            if (index!=0) {
                var player: TextView = findViewById(getResources().getIdentifier("board" + index, "id", packageName))

                if (item!=0) {
                    var drawable: Int
                    if (item > 0)
                        drawable = resources.getIdentifier(String.format("player_%d_%d", item, rand1), "drawable", packageName)
                    else
                        drawable = resources.getIdentifier(String.format("player_%02d_%d", abs(item), rand2),"drawable", packageName)
                    player.setBackgroundResource(drawable)
                }
                else
                    player.setBackgroundResource(R.drawable.board)
            }
        }

        for (num in 1..4) {
            var player1: TextView = findViewById(getResources().getIdentifier("player1_" + num, "id", packageName))
            var player2: TextView = findViewById(getResources().getIdentifier("player2_" + num, "id", packageName))
            val drawable1 = resources.getIdentifier(String.format("player_1_%d", rand1), "drawable", packageName)
            val drawable2 = resources.getIdentifier(String.format("player_01_%d", rand2), "drawable", packageName)
            val nodrawable1 = resources.getIdentifier(String.format("noplayer_1_%d", rand1), "drawable", packageName)
            val nodrawable2 = resources.getIdentifier(String.format("noplayer_01_%d", rand2), "drawable", packageName)

            if (num <= player01)
                player1.setBackgroundResource(drawable1)
            else
                player1.setBackgroundResource(nodrawable1)

            if (num <= player02)
                player2.setBackgroundResource(drawable2)
            else
                player2.setBackgroundResource(nodrawable2)
        }

        checkWin(score01, score02, category, name1, name2)
        showTurn(turn)
    }

    fun checkBoard(turn: Boolean, player01: Int, player02: Int): Int {
        if (turn) {
            return player01
        }
        return player02
    }

    fun checkWin(score01: Int, score02: Int, category: String?, name1: String?, name2: String?) {
        if (score01 == 4 || score02 == 4) {
            var result = ""
            if (score01 == 4) {
                result = "$name1 승리!"
            } else {
                result = "$name2 승리!"
            }

            val intent = Intent(this, GameResultActivity::class.java)
            intent.putExtra("result", result)
            intent.putExtra("category", category)
            startActivity(intent)
            finish()
        }
    }


    fun changeQuestion(category: String, isAdult: String, num: Int) {
        val builder = AlertDialog.Builder(this)
        var Category = category

        if (num==-1 || num==3) {
            Category = "COMMON"
            Log.i(TAG, "공통 카테고리로 변경")
        }

        (application as MasterApplication).service.getQuestion(
            Category, isAdult
        ).enqueue(object : Callback<ArrayList<String>> {
            override fun onResponse(call: Call<ArrayList<String>>, response: Response<ArrayList<String>>) {
                if (response.isSuccessful) {
                    val question = response.body()
                    builder.setTitle("질문").setMessage(question?.get(0).toString())
                        .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id -> }).show()
                } else {
                    Log.e(TAG, "잘못된 카테고리 입니다.")
                }
            }

            override fun onFailure(call: Call<ArrayList<String>>, t: Throwable) {
                Log.e(TAG, "서버 오류")
            }
        })
    }

    fun showTurn(turn: Boolean) {
        if (turn) {
            player1.setBackgroundResource(R.drawable.check_box)
            player2.setBackgroundResource(R.drawable.white_box)
            result_text.setText("player1 차례")
        } else {
            player1.setBackgroundResource(R.drawable.white_box)
            player2.setBackgroundResource(R.drawable.check_box)
            result_text.setText("player2 차례")
        }
    }

    fun showPopup() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.activity_gamerule, null)
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("게임 설명")
            .setPositiveButton("확인") { dialog, which -> }
            .create()
        alertDialog.setView(view)
        alertDialog.show()
    }
}