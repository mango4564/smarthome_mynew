package com.ichippower.smarthousedemo.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.ichippower.smarthousedemo.databinding.ActivityHouseBinding
import com.ichippower.smarthousedemo.mqtt.MqttService
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
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

        /**
         * *
         * 自动登录功能
         */
        val prefs = getSharedPreferences("config", MODE_PRIVATE)
        val lastHouseNumber = prefs.getString("lastHouseNumber","")
        val autoLoginChecked = prefs.getBoolean("autoLoginChecked",false)
        if(autoLoginChecked == true){
            houseNumber = lastHouseNumber.toString()
            val intent = Intent(this, MqttService::class.java)
            intent.putExtra("houseNumber", houseNumber)
            startMqttService()
            startMainActivity()
            Toast.makeText(this,"自动登录成功,小屋编号:"+ houseNumber,Toast.LENGTH_SHORT).show()
        }



        binding.confirmButton.setOnClickListener {
            val _houseNumber = binding.houseNumber.text.toString()
            if (_houseNumber == "") {
                Toast.makeText(applicationContext, "编号不能为空！", Toast.LENGTH_SHORT).show()
            } else {
                getSharedPreferences("config", MODE_PRIVATE).edit{
                    putString("lastHouseNumber",binding.houseNumber.text.toString())
                    val autoLoginCheckBox = binding.autoLoginCheckBox
                    if(autoLoginCheckBox.isChecked){
                        putBoolean("autoLoginChecked",true)
                    }else{
                        putBoolean("autoLoginChecked",false)
                    }
                }
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
            MqttService.subscribeTopic = "smarthouse_remote_subscribe_$houseNumber"
            MqttService.publishTopic = "smarthouse_subscribe_$houseNumber"
            MqttService.subscribe()
        } else {
            MqttService.CLIENTID = UUID.randomUUID().toString()
            MqttService.subscribeTopic = "smarthouse_remote_subscribe_$houseNumber"
            MqttService.publishTopic = "smarthouse_subscribe_$houseNumber"
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