package com.ichippower.smarthousedemo.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ichippower.smarthousedemo.databinding.ActivityHouseBinding
import com.ichippower.smarthousedemo.mqtt.MqttService
import java.util.UUID

class HouseActivity : AppCompatActivity() {
    private var isExit = false
    private var handler: Handler? = null
    private lateinit var binding: ActivityHouseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHouseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermission()

        binding.confirmButton.setOnClickListener {
            val _houseNumber = binding.houseNumber.text.toString()
            if (_houseNumber == "") {
                Toast.makeText(applicationContext, "编号不能为空！", Toast.LENGTH_SHORT).show()
            } else {
                houseNumber = _houseNumber
                startMqttService()
                startMainActivity()
            }
        }

        handler = @SuppressLint("HandlerLeak")
        object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                isExit = false
            }
        }
    }

    private fun startMqttService() {
        if (MqttService.getServiceConnStat()) {
            MqttService.subscribeTopic = "smarthouse_remote_subscribe*$houseNumber"
            MqttService.publishTopic = "smarthouse_subscribe*$houseNumber"
            MqttService.subscribe()
        } else {
            MqttService.CLIENTID = UUID.randomUUID().toString()
            MqttService.subscribeTopic = "smarthouse_remote_subscribe*$houseNumber"
            MqttService.publishTopic = "smarthouse_subscribe*$houseNumber"
            MqttService.startService(applicationContext)
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun checkPermission() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WAKE_LOCK
        )
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, permissions, 200)
                return
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                isExit = true
                handler!!.sendEmptyMessageDelayed(0, 1500)
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show()
                return false
            } else {
                exitAPP()
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun exitAPP() {
        val activityManager =
            this.applicationContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val appTaskList = activityManager.appTasks
        for (appTask in appTaskList) {
            appTask.finishAndRemoveTask()
        }
    }

    companion object {
        lateinit var houseNumber: String
    }
}