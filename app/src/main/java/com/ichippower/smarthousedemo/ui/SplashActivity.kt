package com.ichippower.smarthousedemo.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ichippower.smarthousedemo.R
import java.util.Timer
import java.util.TimerTask


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)
        startHouseActivity()
    }

    private fun startHouseActivity() {
        val timer = Timer()
        val intent = Intent(this, HouseActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

        class MyTimerTask : TimerTask() {
            override fun run() {
                startActivity(intent)
            }
        }

        val timerTask = MyTimerTask()
        timer.schedule(timerTask, 1000)
    }
}