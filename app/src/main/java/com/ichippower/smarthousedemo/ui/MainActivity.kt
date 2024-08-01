package com.ichippower.smarthousedemo.ui

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.KeyEvent
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ichippower.smarthousedemo.R
import com.ichippower.smarthousedemo.databinding.ActivityMainBinding
import com.ichippower.smarthousedemo.mqtt.MqttService

class MainActivity : AppCompatActivity() {
    private var isExit = false
    private var handler: Handler? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations
        navController.addOnDestinationChangedListener(
            ActionBarOnDestinationChangedListener(this)
        )
        navView.setupWithNavController(navController)

        @SuppressLint("ResourceType")
        val csl = ContextCompat.getColorStateList(this, R.drawable.bottom_btn_selected_color)
        navView.itemTextColor = csl

        handler = @SuppressLint("HandlerLeak")
        object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                isExit = false
            }
        }

        val iv: ImageView = binding.disconnect
        iv.setOnClickListener {
            MqttService.unsubscribe()
            val intent = Intent(this, HouseActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                isExit = true
                handler!!.sendEmptyMessageDelayed(0, 1500)
                Toast.makeText(applicationContext, "再按一次退出程序", Toast.LENGTH_SHORT).show()
                return false
            } else {
                exitAPP()
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun exitAPP() {
        val activityManager =
            applicationContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val appTaskList = activityManager.appTasks
        for (appTask in appTaskList) {
            appTask.finishAndRemoveTask()
        }
    }

    class ActionBarOnDestinationChangedListener(
        private val activity: AppCompatActivity
    ) : NavController.OnDestinationChangedListener {
        override fun onDestinationChanged(
            controller: NavController,
            destination: NavDestination,
            arguments: Bundle?
        ) {
            val label = destination.fillInLabel(activity, arguments)
            if (label != null) {
                activity.findViewById<TextView>(R.id.toolbarTitle).text = label
                activity.supportActionBar?.title = ""
            }
        }
    }
}

