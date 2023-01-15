package com.smu.som

import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
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

        val sp = this.getSharedPreferences("game_sp", Context.MODE_PRIVATE)
        val isAdult = sp.getString("isAdult", "n")
        val email = sp.getString("email", null)
        val sound = sp.getBoolean("sound", false)
        val categoryArray = arrayOf("홈으로", "관계 선택으로", "다시하기") // 리스트에 들어갈 Array
        val SIZE = 30
        var arr = IntArray(SIZE, { 0 } )
        var yuts = IntArray(6, { 0 } )
        var players: ArrayList<TextView> = ArrayList()
        var player1 = 4
        var player2 = 4
        var score1 = 0
        var score2 = 0
        var catch1 = false
        var catch2 = false
        var turn = true
        var used = arrayOf<Int>()
        var pass = arrayOf<Int>()

        var builder = AlertDialog.Builder(this)
        val soundPool = SoundPool.Builder().build()
        val gamesound = IntArray(8, { 0 } )
        val category = intent.getStringExtra("category")
        var kcategory = intent.getStringExtra("kcategory")
        val name_1p = intent.getStringExtra("name1")
        val name_2p = intent.getStringExtra("name2")

        var char1 = sp.getInt("character1", 0)
        var char2 = sp.getInt("character2", 11)
        var rand1 = char1 + 1
        var rand2 = char2 + 1

        name1.text = name_1p
        name2.text = name_2p
        Category.text = kcategory

        for (i in 0..SIZE) {
            players.add(findViewById(getResources().getIdentifier("board" + i, "id", packageName)))
        }

        if (sound) {
            for (i in 0..7) {
                gamesound[i] = soundPool.load(this, resources.getIdentifier("sound_$i", "raw", packageName), 1)
            }
        }

        stop.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("일시정지").setItems(categoryArray, DialogInterface.OnClickListener { dialog, which ->
                        if (which == 0) {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else if (which == 1) {
                            val intent = Intent(this, GameSettingActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(this, GameActivity::class.java)
                            intent.putExtra("category", category)
                            intent.putExtra("kcategory", kcategory)
                            intent.putExtra("name1", name_1p)
                            intent.putExtra("name2", name_2p)
                            startActivity(intent)
                            finish()
                        }
                    }).setNegativeButton("취소", DialogInterface.OnClickListener { dialog, id -> })
            builder.show()
        }

        game_rule.setOnClickListener { showPopup() }

        drawGame(arr, player1, player2, score1, score2, turn, rand1, rand2, category, kcategory, name_1p, name_2p, email, used, pass)
        yut.setBackgroundResource(R.drawable.pick)

        yut.setOnClickListener {
            yut.setBackgroundResource(R.drawable.nopick)
            yut.isClickable = false
            soundPool.play(gamesound[6], 1.0f, 1.0f, 0, 0, 1.0f)

            var num = playGame(soundPool, gamesound, yuts.sum())

            if (num == 4 || num== 5) {
                yut.isClickable = true
                builder.setTitle("한 번 더!").setPositiveButton("확인", null).show()
                yut.setBackgroundResource(R.drawable.pick)
            }

            if (num == 0 && checkGo(arr, turn) && yuts.sum() == 0) {
                yut.isClickable = true
                start.isClickable = false
                builder.setTitle("한 번 더 던지세요!").setPositiveButton("확인", null).show()
                yut.setBackgroundResource(R.drawable.pick)
                start.setBackgroundResource(R.drawable.nopick)
            } else {
                yuts[num] += 1
                if (num != 4 && num!= 5) {
                    var builder = AlertDialog.Builder(this)
                    var ecategory = category
                    if (num==-1 || num==3) {
                        ecategory = "COMMON"
                        Log.i(TAG, "공통 카테고리로 변경")
                    }

                    (application as MasterApplication).service.getQuestion(
                        ecategory!!, isAdult!!
                    ).enqueue(object : Callback<ArrayList<Question>> {
                        override fun onResponse(call: Call<ArrayList<Question>>, response: Response<ArrayList<Question>>) {
                            if (response.isSuccessful) {
                                val question = response.body()
                                val questionId = question?.get(0)!!.id

                                builder.setTitle("질문").setMessage(question?.get(0)?.question.toString())
                                    .setPositiveButton("답변", DialogInterface.OnClickListener { dialog, id ->
                                        used = used.plus(questionId)
                                    }).setNegativeButton("질문 변경", DialogInterface.OnClickListener { dialog, id ->
                                        builder.setMessage(question?.get(1)?.question.toString())
                                            .setPositiveButton("답변", DialogInterface.OnClickListener { dialog, id ->
                                                used = used.plus(questionId)
                                                turn = !turn
                                                yuts = IntArray(6, { 0 } )
                                                yut.setBackgroundResource(R.drawable.pick)
                                                start.setBackgroundResource(R.drawable.nopick)
                                                drawGame(arr, player1, player2, score1, score2, turn, rand1, rand2, category, kcategory, name_1p, name_2p, email, used, pass)
                                                start.setOnClickListener(null)
                                                for ((index,item) in arr.withIndex())
                                                    if (item!=0 && index!=0)
                                                        players[index]?.setOnClickListener(null)
                                                yut.isClickable = true
                                                pass = pass.plus(questionId)
                                            })
                                            .setNegativeButton("", null).show()
                                    })

                                if (turn) {
                                    if (catch1) {
                                        catch1 = false
                                        builder.setPositiveButton("추가질문권", DialogInterface.OnClickListener { dialog, id ->
                                            used = used.plus(questionId)
                                        }).setNegativeButton("패스", DialogInterface.OnClickListener { dialog, id ->
                                            pass = pass.plus(questionId)
                                        })
                                    }
                                }
                                else {
                                    if (catch2) {
                                        catch2 = false
                                        builder.setPositiveButton("추가질문권", DialogInterface.OnClickListener { dialog, id ->
                                            used = used.plus(questionId)
                                        }).setNegativeButton("패스", DialogInterface.OnClickListener { dialog, id ->
                                            pass = pass.plus(questionId)
                                        })
                                    }
                                }
                                builder.setCancelable(false).show()
                            } else {
                                Log.e(TAG, "잘못된 카테고리 입니다.")
                            }
                        }

                        override fun onFailure(call: Call<ArrayList<Question>>, t: Throwable) {
                            Log.e(TAG, "서버 오류")
                        }
                    })
                }
            }


            if (yuts.sum() == 1 && num != 4 && num != 5) {
                showResult(turn, num)
                if (num != 0 && checkBoard(turn, player1, player2) != 0) {
                    start.setBackgroundResource(R.drawable.pick)
                }

                for ((index,item) in arr.withIndex()) {
                    if (item!=0 && index!=0) {
                        if (turn == item > 0) {
                            var pick: LinearLayout = findViewById(getResources().getIdentifier("pick" + index, "id", packageName))
                            pick.setBackgroundResource(R.drawable.pick)
                        }

                        players[index].setOnClickListener {
                            if (turn == item > 0) {
                                yuts[num] -= 1
                                if (num == 0)
                                    num = -1
                                var idx = getIndex(index, num)

                                if (turn) {
                                    if (arr[idx] < 0 && idx != 0) {     // 말을 잡을 경우
                                        player2 -= arr[idx]
                                        arr[idx] = item
                                        catch1 = true
                                        soundPool.play(gamesound[7], 1.0f, 1.0f, 0, 0, 1.0f)
                                        showCatch()
                                    }
                                    else {
                                        if (idx == 0)
                                            score1 += item
                                        else
                                            arr[idx] += item
                                        turn = !turn
                                        yuts = IntArray(6, { 0 } )
                                        yut.setBackgroundResource(R.drawable.pick)
                                        start.setBackgroundResource(R.drawable.nopick)
                                    }
                                }
                                else {
                                    if (arr[idx] > 0 && idx != 0) {     // 말을 잡을 경우
                                        player1 += arr[idx]
                                        arr[idx] = item
                                        catch2 = true
                                        soundPool.play(gamesound[7], 1.0f, 1.0f, 0, 0, 1.0f)
                                        showCatch()
                                    }
                                    else {
                                        if (idx == 0)
                                            score2 -= item
                                        else
                                            arr[idx] += item
                                        turn = !turn
                                        yuts = IntArray(6, { 0 } )
                                        yut.setBackgroundResource(R.drawable.pick)
                                        start.setBackgroundResource(R.drawable.nopick)
                                    }
                                }
                                arr[index] = 0
                                drawGame(arr, player1, player2, score1, score2, turn, rand1, rand2, category, kcategory, name_1p, name_2p, email, used, pass)

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
                    if (num != 0 && checkBoard(turn, player1, player2) != 0) {
                        yuts[num] -= 1
                        if (num == 0)
                            num = -1
                        if (turn) {
                            if (arr[num] < 0) {     // 말을 잡을 경우
                                player2 -= arr[num]
                                arr[num] = 1
                                catch1 = true
                                soundPool.play(gamesound[7], 1.0f, 1.0f, 0, 0, 1.0f)
                                showCatch()
                            }
                            else {
                                arr[num] += 1
                                turn = !turn
                                yuts = IntArray(6, { 0 } )
                                yut.setBackgroundResource(R.drawable.pick)
                                start.setBackgroundResource(R.drawable.nopick)
                            }
                            player1 -= 1
                        }
                        else {
                            if (arr[num] > 0) {     // 말을 잡을 경우
                                player1 += arr[num]
                                arr[num] = -1
                                catch2 = true
                                soundPool.play(gamesound[7], 1.0f, 1.0f, 0, 0, 1.0f)
                                showCatch()
                            }
                            else {
                                arr[num] -= 1
                                turn = !turn
                                yuts = IntArray(6, { 0 } )
                                yut.setBackgroundResource(R.drawable.pick)
                                start.setBackgroundResource(R.drawable.nopick)
                            }
                            player2 -= 1
                        }
                        drawGame(arr, player1, player2, score1, score2, turn, rand1, rand2, category, kcategory, name_1p, name_2p, email, used, pass)

                        start.setOnClickListener(null)
                        for ((index,item) in arr.withIndex())
                            if (item!=0 && index!=0)
                                players[index]?.setOnClickListener(null)
                        yut.isClickable = true
                    }
                }
            }
            else {
                showResult(turn, num)

                for ((index,item) in arr.withIndex()) {
                    if (item!=0 && index!=0 && yuts.sum() == 2) {
                        if (turn == item > 0) {
                            var pick: LinearLayout = findViewById(getResources().getIdentifier("pick" + index, "id", packageName))
                            pick.setBackgroundResource(R.drawable.pick)
                        }

                        players[index].setOnClickListener {
                            if (turn == item > 0) {
                                var builder2 = AlertDialog.Builder(this)
                                var size = 0
                                var yutArray = arrayOf("", "", "", "", "", "")
                                var yutss: ArrayList<Int> = ArrayList()
                                for ((index,item) in yuts.withIndex()) {
                                    if (item > 0) {
                                        yutss.add(index)
                                        yutArray[size] = "$index * $item"
                                        size += 1
                                    }
                                }
                                yutArray = yutArray.sliceArray(0..size - 1)
                                builder2.setTitle("윷 선택").setItems(yutArray, DialogInterface.OnClickListener { dialog, which ->
                                    num = yutss[which]

                                    yuts[num] -= 1
                                    if (num == 0)
                                        num = -1
                                    var idx = getIndex(index, num)

                                    if (turn) {
                                        if (arr[idx] < 0 && idx != 0) {     // 말을 잡을 경우
                                            player2 -= arr[idx]
                                            arr[idx] = item
                                            catch1 = true
                                            soundPool.play(gamesound[7], 1.0f, 1.0f, 0, 0, 1.0f)
                                            showCatch()
                                        }
                                        else {
                                            if (idx == 0)
                                                score1 += item
                                            else
                                                arr[idx] += item
                                            if (yuts.sum() == 0) {
                                                turn = !turn
                                                yuts = IntArray(6, { 0 })
                                                yut.setBackgroundResource(R.drawable.pick)
                                                start.setBackgroundResource(R.drawable.nopick)
                                                start.setOnClickListener(null)
                                                for ((index,item) in arr.withIndex())
                                                    if (item!=0 && index!=0)
                                                        players[index]?.setOnClickListener(null)
                                                yut.isClickable = true
                                            }
                                        }
                                    }
                                    else {
                                        if (arr[idx] > 0 && idx != 0) {     // 말을 잡을 경우
                                            player1 += arr[idx]
                                            arr[idx] = item
                                            catch2 = true
                                            soundPool.play(gamesound[7], 1.0f, 1.0f, 0, 0, 1.0f)
                                            showCatch()
                                        }
                                        else {
                                            if (idx == 0)
                                                score2 -= item
                                            else
                                                arr[idx] += item
                                            if (yuts.sum() == 0) {
                                                turn = !turn
                                                yuts = IntArray(6, { 0 })
                                                yut.setBackgroundResource(R.drawable.pick)
                                                start.setBackgroundResource(R.drawable.nopick)
                                                start.setOnClickListener(null)
                                                for ((index,item) in arr.withIndex())
                                                    if (item!=0 && index!=0)
                                                        players[index]?.setOnClickListener(null)
                                                yut.isClickable = true
                                            }
                                        }
                                    }
                                    arr[index] = 0
                                    drawGame(arr, player1, player2, score1, score2, turn, rand1, rand2, category, kcategory, name_1p, name_2p, email, used, pass)

                                    num = findOne(yuts)

                                    for ((index,item) in arr.withIndex()) {
                                        if (item!=0 && index!=0) {
                                            if (turn == item > 0) {
                                                var pick: LinearLayout = findViewById(getResources().getIdentifier("pick" + index, "id", packageName))
                                                pick.setBackgroundResource(R.drawable.pick)
                                            }

                                            players[index].setOnClickListener {
                                                if (turn == item > 0) {
                                                    yuts[num] -= 1
                                                    if (num == 0)
                                                        num = -1
                                                    var idx = getIndex(index, num)

                                                    if (turn) {
                                                        if (arr[idx] < 0 && idx != 0) {     // 말을 잡을 경우
                                                            player2 -= arr[idx]
                                                            arr[idx] = item
                                                            catch1 = true
                                                            soundPool.play(gamesound[7], 1.0f, 1.0f, 0, 0, 1.0f)
                                                            showCatch()
                                                        }
                                                        else {
                                                            if (idx == 0)
                                                                score1 += item
                                                            else
                                                                arr[idx] += item
                                                            turn = !turn
                                                            yuts = IntArray(6, { 0 } )
                                                            yut.setBackgroundResource(R.drawable.pick)
                                                            start.setBackgroundResource(R.drawable.nopick)
                                                        }
                                                    }
                                                    else {
                                                        if (arr[idx] > 0 && idx != 0) {     // 말을 잡을 경우
                                                            player1 += arr[idx]
                                                            arr[idx] = item
                                                            catch2 = true
                                                            soundPool.play(gamesound[7], 1.0f, 1.0f, 0, 0, 1.0f)
                                                            showCatch()
                                                        }
                                                        else {
                                                            if (idx == 0)
                                                                score2 -= item
                                                            else
                                                                arr[idx] += item
                                                            turn = !turn
                                                            yuts = IntArray(6, { 0 } )
                                                            yut.setBackgroundResource(R.drawable.pick)
                                                            start.setBackgroundResource(R.drawable.nopick)
                                                        }
                                                    }
                                                    arr[index] = 0
                                                    drawGame(arr, player1, player2, score1, score2, turn, rand1, rand2, category, kcategory, name_1p, name_2p, email, used, pass)

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
                                        if (num != 0 && checkBoard(turn, player1, player2) != 0) {
                                            yuts[num] -= 1
                                            if (num == 0)
                                                num = -1
                                            if (turn) {
                                                if (arr[num] < 0) {     // 말을 잡을 경우
                                                    player2 -= arr[num]
                                                    arr[num] = 1
                                                    catch1 = true
                                                    soundPool.play(gamesound[7], 1.0f, 1.0f, 0, 0, 1.0f)
                                                    showCatch()
                                                }
                                                else {
                                                    arr[num] += 1
                                                    turn = !turn
                                                    yuts = IntArray(6, { 0 } )
                                                    yut.setBackgroundResource(R.drawable.pick)
                                                    start.setBackgroundResource(R.drawable.nopick)
                                                }
                                                player1 -= 1
                                            }
                                            else {
                                                if (arr[num] > 0) {     // 말을 잡을 경우
                                                    player1 += arr[num]
                                                    arr[num] = -1
                                                    catch2 = true
                                                    soundPool.play(gamesound[7], 1.0f, 1.0f, 0, 0, 1.0f)
                                                    showCatch()
                                                }
                                                else {
                                                    arr[num] -= 1
                                                    turn = !turn
                                                    yuts = IntArray(6, { 0 } )
                                                    yut.setBackgroundResource(R.drawable.pick)
                                                    start.setBackgroundResource(R.drawable.nopick)
                                                }
                                                player2 -= 1
                                            }
                                            drawGame(arr, player1, player2, score1, score2, turn, rand1, rand2, category, kcategory, name_1p, name_2p, email, used, pass)

                                            start.setOnClickListener(null)
                                            for ((index,item) in arr.withIndex())
                                                if (item!=0 && index!=0)
                                                    players[index]?.setOnClickListener(null)
                                            yut.isClickable = true
                                        }
                                    }
                                }).setNegativeButton("취소", null).show()
                            }
                        }
                    }
                }


                start.setOnClickListener {
                    if (yuts.sum() == 1) {
                        Log.d(TAG, "선택지 1개")
                    }

                    var builder2 = AlertDialog.Builder(this)
                    var size = 0
                    var yutArray = arrayOf("", "", "", "", "", "")
                    var yutss: ArrayList<Int> = ArrayList()
                    for ((index,item) in yuts.withIndex()) {
                        if (item > 0 && index != 0) {
                            yutss.add(index)
                            yutArray[size] = "$index * $item"
                            size += 1
                        }
                    }
                    yutArray = yutArray.sliceArray(0..size - 1)
                    builder2.setTitle("윷 선택").setItems(yutArray, DialogInterface.OnClickListener { dialog, which ->
                        num = yutss[which]
                        if (checkBoard(turn, player1, player2) != 0) {
                            yuts[num] -= 1
                            if (turn) {
                                if (arr[num] < 0) {     // 말을 잡을 경우
                                    player2 -= arr[num]
                                    arr[num] = 1
                                    catch1 = true
                                    soundPool.play(gamesound[7], 1.0f, 1.0f, 0, 0, 1.0f)
                                    showCatch()
                                    yut.isClickable = true
                                }
                                else {
                                    arr[num] += 1
                                    if (yuts.sum() == 0) {
                                        turn = !turn
                                        yuts = IntArray(6, { 0 } )
                                        yut.setBackgroundResource(R.drawable.pick)
                                        start.setBackgroundResource(R.drawable.nopick)
                                        start.setOnClickListener(null)
                                        for ((index,item) in arr.withIndex())
                                            if (item!=0 && index!=0)
                                                players[index]?.setOnClickListener(null)
                                        yut.isClickable = true
                                    }
                                }
                                player1 -= 1
                            }
                            else {
                                if (arr[num] > 0) {     // 말을 잡을 경우
                                    player1 += arr[num]
                                    arr[num] = -1
                                    catch2 = true
                                    soundPool.play(gamesound[7], 1.0f, 1.0f, 0, 0, 1.0f)
                                    showCatch()
                                    yut.isClickable = true
                                }
                                else {
                                    arr[num] -= 1
                                    if (yuts.sum() == 0) {
                                        turn = !turn
                                        yuts = IntArray(6, { 0 })
                                        yut.setBackgroundResource(R.drawable.pick)
                                        start.setBackgroundResource(R.drawable.nopick)
                                        start.setOnClickListener(null)
                                        for ((index,item) in arr.withIndex())
                                            if (item!=0 && index!=0)
                                                players[index]?.setOnClickListener(null)
                                        yut.isClickable = true
                                    }
                                }
                                player2 -= 1
                            }
                            drawGame(arr, player1, player2, score1, score2, turn, rand1, rand2, category, kcategory, name_1p, name_2p, email, used, pass)
                            if (yuts.sum() == 1 && yuts[0] == 1) {
                                start.setBackgroundResource(R.drawable.nopick)
                                start.isClickable = false
                            }
                        }

                        num = findOne(yuts)

                        for ((index,item) in arr.withIndex()) {
                            if (item!=0 && index!=0) {
                                if (turn == item > 0) {
                                    var pick: LinearLayout = findViewById(getResources().getIdentifier("pick" + index, "id", packageName))
                                    pick.setBackgroundResource(R.drawable.pick)
                                }

                                players[index].setOnClickListener {
                                    if (turn == item > 0) {
                                        yuts[num] -= 1
                                        if (num == 0)
                                            num = -1
                                        var idx = getIndex(index, num)

                                        if (turn) {
                                            if (arr[idx] < 0 && idx != 0) {     // 말을 잡을 경우
                                                player2 -= arr[idx]
                                                arr[idx] = item
                                                catch1 = true
                                                soundPool.play(gamesound[7], 1.0f, 1.0f, 0, 0, 1.0f)
                                                showCatch()
                                            }
                                            else {
                                                if (idx == 0)
                                                    score1 += item
                                                else
                                                    arr[idx] += item
                                                turn = !turn
                                                yuts = IntArray(6, { 0 } )
                                                yut.setBackgroundResource(R.drawable.pick)
                                                start.setBackgroundResource(R.drawable.nopick)
                                            }
                                        }
                                        else {
                                            if (arr[idx] > 0 && idx != 0) {     // 말을 잡을 경우
                                                player1 += arr[idx]
                                                arr[idx] = item
                                                catch2 = true
                                                soundPool.play(gamesound[7], 1.0f, 1.0f, 0, 0, 1.0f)
                                                showCatch()
                                            }
                                            else {
                                                if (idx == 0)
                                                    score2 -= item
                                                else
                                                    arr[idx] += item
                                                turn = !turn
                                                yuts = IntArray(6, { 0 } )
                                                yut.setBackgroundResource(R.drawable.pick)
                                                start.setBackgroundResource(R.drawable.nopick)
                                            }
                                        }
                                        arr[index] = 0
                                        drawGame(arr, player1, player2, score1, score2, turn, rand1, rand2, category, kcategory, name_1p, name_2p, email, used, pass)

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
                            if (num != 0 && checkBoard(turn, player1, player2) != 0) {
                                yuts[num] -= 1
                                if (num == 0)
                                    num = -1
                                if (turn) {
                                    if (arr[num] < 0) {     // 말을 잡을 경우
                                        player2 -= arr[num]
                                        arr[num] = 1
                                        catch1 = true
                                        soundPool.play(gamesound[7], 1.0f, 1.0f, 0, 0, 1.0f)
                                        showCatch()
                                    }
                                    else {
                                        arr[num] += 1
                                        turn = !turn
                                        yuts = IntArray(6, { 0 } )
                                        yut.setBackgroundResource(R.drawable.pick)
                                        start.setBackgroundResource(R.drawable.nopick)
                                    }
                                    player1 -= 1
                                }
                                else {
                                    if (arr[num] > 0) {     // 말을 잡을 경우
                                        player1 += arr[num]
                                        arr[num] = -1
                                        catch2 = true
                                        soundPool.play(gamesound[7], 1.0f, 1.0f, 0, 0, 1.0f)
                                        showCatch()
                                    }
                                    else {
                                        arr[num] -= 1
                                        turn = !turn
                                        yuts = IntArray(6, { 0 } )
                                        yut.setBackgroundResource(R.drawable.pick)
                                        start.setBackgroundResource(R.drawable.nopick)
                                    }
                                    player2 -= 1
                                }
                                drawGame(arr, player1, player2, score1, score2, turn, rand1, rand2, category, kcategory, name_1p, name_2p, email, used, pass)

                                start.setOnClickListener(null)
                                for ((index,item) in arr.withIndex())
                                    if (item!=0 && index!=0)
                                        players[index]?.setOnClickListener(null)
                                yut.isClickable = true
                            }
                        }
                    }).setNegativeButton("취소", null).show()
                }

                start.isClickable = false
                if (yuts.sum() == 2) {
                    start.setBackgroundResource(R.drawable.pick)
                    start.isClickable = true
                }
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

    fun playGame(soundPool: SoundPool, gamesound: IntArray, sum: Int): Int {
        val yuts = arrayOf("빽도", "도", "개", "걸", "윷", "모")
        var num = percentage(sum)
        result.setBackgroundResource(resources.getIdentifier("result_$num", "drawable", packageName))
        Handler(Looper.getMainLooper()).postDelayed({
            soundPool.play(gamesound[num], 1.0f, 1.0f, 0, 0, 1.0f)
            result_text.setText(yuts[num])
            result.setBackgroundResource(resources.getIdentifier("yut_$num", "drawable", packageName))
        }, 1000)
        return num
    }

    fun percentage(sum: Int): Int {
//        val per = arrayOf(1, 3, 6, 4, 1, 1)
        var n = 5
        var per = arrayOf(6, 1, 1, 1, 3, 4)
        if (sum > 0) {
            Log.d("per", "확률 조작")
            per = arrayOf(1, 4, 6, 5)
            n = 3
        }
        val range = (1..16)
        var num = range.random()

        for ((index,item) in per.withIndex()) {
            if (num <= item)
                return index
            num -= item
        }
        return n
    }

    fun findOne(array: IntArray): Int {
        for ((index, item) in array.withIndex()) {
            if (item == 1)
                return index
        }
        return 1
    }

    fun drawGame(array: IntArray, player01: Int, player02: Int, score01: Int, score02: Int, turn: Boolean, rand1: Int, rand2: Int, category: String?, kcategory: String?, name1: String?, name2: String?, email: String?, used: Array<Int>, pass: Array<Int>) {
        for ((index,item) in array.withIndex()) {
            if (index!=0) {
                var player: TextView = findViewById(getResources().getIdentifier("board" + index, "id", packageName))
                var pick: LinearLayout = findViewById(getResources().getIdentifier("pick" + index, "id", packageName))
                pick.setBackgroundResource(R.drawable.nopick)

                if (item!=0) {
                    var drawable: Int
                    if (item > 0)
                        drawable = resources.getIdentifier(String.format("player_%d_%d", item, rand1), "drawable", packageName)
                    else
                        drawable = resources.getIdentifier(String.format("player_%d_%d", abs(item), rand2),"drawable", packageName)
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
            val drawable2 = resources.getIdentifier(String.format("player_1_%d", rand2), "drawable", packageName)
            val nodrawable1 = resources.getIdentifier(String.format("noplayer_%d", rand1), "drawable", packageName)
            val nodrawable2 = resources.getIdentifier(String.format("noplayer_%d", rand2), "drawable", packageName)

            if (num <= player01)
                player1.setBackgroundResource(drawable1)
            else
                player1.setBackgroundResource(nodrawable1)

            if (num <= player02)
                player2.setBackgroundResource(drawable2)
            else
                player2.setBackgroundResource(nodrawable2)
        }
        for (num in 0..5) {
            var result1: TextView = findViewById(getResources().getIdentifier(true.toString()+num.toString(), "id", packageName))
            var result2: TextView = findViewById(getResources().getIdentifier(false.toString()+num.toString(), "id", packageName))
            result1.setBackgroundResource(R.drawable.nopick)
            result2.setBackgroundResource(R.drawable.nopick)
        }
        score1.text = score01.toString()
        score2.text = score02.toString()
        checkWin(score01, score02, category, kcategory, name1, name2, email, used, pass)
        showTurn(turn, name1, name2)
    }

    fun checkBoard(turn: Boolean, player01: Int, player02: Int): Int {
        if (turn) {
            return player01
        }
        return player02
    }

    fun checkWin(score01: Int, score02: Int, category: String?, kcategory: String?, name1: String?, name2: String?, email: String?, used: Array<Int>, pass: Array<Int>) {
        if (score01 == 4 || score02 == 4) {
            var result = ""
            if (score01 == 4) {
                result = "$name1 승리!"
            } else {
                result = "$name2 승리!"
            }
            email?.let { category?.let { it1 -> saveResult(it, it1, used, pass) } }

            val intent = Intent(this, GameResultActivity::class.java)
            intent.putExtra("result", result)
            intent.putExtra("kcategory", kcategory)
            intent.putExtra("category", category)
            intent.putExtra("name1", name1)
            intent.putExtra("name2", name2)
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(intent)
                finish()
            }, 1000)
        }
    }

    fun checkGo(array: IntArray, turn: Boolean): Boolean {
        for ((index, item) in array.withIndex()) {
            if (index != 0 && item != 0) {
                if (turn == item > 0) {
                    return false
                }
            }
        }
        return true
    }

    fun showTurn(turn: Boolean, name1: String?, name2: String?) {
        if (turn) {
            player1.setBackgroundResource(R.drawable.check_box)
            player2.setBackgroundResource(R.drawable.white_box)
            result_text.setText("$name1 차례")
        } else {
            player1.setBackgroundResource(R.drawable.white_box)
            player2.setBackgroundResource(R.drawable.check_box)
            result_text.setText("$name2 차례")
        }
    }

    fun showResult(turn: Boolean, num: Int) {
        var result: TextView = findViewById(getResources().getIdentifier(turn.toString()+num.toString(), "id", packageName))
        result.setBackgroundResource(resources.getIdentifier("result$num", "drawable", packageName))
    }

    fun showPopup() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view1 = inflater.inflate(R.layout.activity_gamerule1, null)
        val alertDialog1 = AlertDialog.Builder(this)
            .setTitle("게임 방법")
            .setPositiveButton("다음") { dialog, which ->
                val view2 = inflater.inflate(R.layout.activity_gamerule2, null)
                val alertDialog2 = AlertDialog.Builder(this).setTitle("게임 방법")
                    .setPositiveButton("확인", null)
                    .setNegativeButton("취소", null)
                alertDialog2.setView(view2)
                alertDialog2.setCancelable(false).show()
            }
            .setNegativeButton("취소", null)
        alertDialog1.setView(view1)
        alertDialog1.setCancelable(false).show()
    }

    fun showCatch() {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("말을 잡았습니다!")
            .setIcon(R.drawable.ccatch)
            .setPositiveButton("확인") { dialog, which -> }
            .create()
        alertDialog.show()
        yut.setBackgroundResource(R.drawable.pick)
        start.setBackgroundResource(R.drawable.nopick)
    }

    fun saveResult(email: String, category: String, used: Array<Int>, pass: Array<Int>) {
        val result = GameResult(used, pass)
        var ecategory = category
        if (category == "PARENT") {
            ecategory = "FAMILY"
        }
        (application as MasterApplication).service.saveResult(
            email, ecategory, result
        ).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    Log.e(TAG, "저장 완료")
                } else {
                    Log.e(TAG, "저장 오류")
                    Log.e(TAG, response.body().toString())
                    Log.e(TAG, response.code().toString())
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.e(TAG, "서버 오류")
            }
        })
    }
}