package com.ichippower.smarthousedemo.ui.about

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ichippower.smarthousedemo.R
import com.ichippower.smarthousedemo.databinding.FragmentAboutBinding
import com.ichippower.smarthousedemo.mqtt.MqttService
import com.ichippower.smarthousedemo.mqtt.RequestParam
import com.ichippower.smarthousedemo.ui.HouseActivity
import com.llw.dialog.CustomDialog
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class AboutFragment : Fragment() {
    private var _binding: FragmentAboutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var customDialog: CustomDialog?= null
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val button_leavingHome = binding.leavingHomeButton
        val button_home = binding.HomeButton
        val devices = arrayOf("客厅的灯", "浴室的灯", "厨房的灯", "房梁的灯", "抽油烟机", "卧室的灯", "热水器", "浴霸", "排气扇")
        val checkedDevices = booleanArrayOf(true,true,true,true,true,true,true,true,true)
        val layout_leavingHome = binding.leavingHomeLayout
        val layout_home = binding.homeLayout
        var device_to_be_closed = mutableListOf("客厅的灯", "浴室的灯", "厨房的灯", "房梁的灯", "抽油烟机", "卧室的灯", "热水器", "浴霸", "排气扇")
        var device_to_be_opened = mutableListOf("客厅的灯", "浴室的灯", "厨房的灯", "房梁的灯", "抽油烟机", "卧室的灯", "热水器", "浴霸", "排气扇")

        layout_leavingHome.setOnClickListener{
            device_to_be_closed.clear()
            val builder = AlertDialog.Builder(context)
            builder.setTitle("选择要关闭的设备：")
            builder.setMultiChoiceItems(devices, checkedDevices) { dialog, which, isChecked ->
                checkedDevices[which] = isChecked
            }
            builder.setPositiveButton("OK") {dialog, which ->
                // Handle OK button click
                // Process the selected devices
                for (i in checkedDevices.indices) {
                    if (checkedDevices[i]) {
                        device_to_be_closed.add(devices[i])
                    }
                }
            }
            builder.setNegativeButton("Cancel") { dialog, which ->
                // Handle Cancel button click
            }

            val dialog = builder.create()
            dialog.show()
        }
        layout_home.setOnClickListener{
            device_to_be_opened.clear()
            val builder = AlertDialog.Builder(context)
            builder.setTitle("选择要打开的设备：")
            builder.setMultiChoiceItems(devices, checkedDevices) { dialog, which, isChecked ->
                checkedDevices[which] = isChecked
            }
            builder.setPositiveButton("OK") {dialog, which ->
                // Handle OK button click
                // Process the selected devices
                for (i in checkedDevices.indices) {
                    if (checkedDevices[i]) {
                        device_to_be_opened.add(devices[i])
                    }
                }
            }
            builder.setNegativeButton("Cancel") { dialog, which ->
                // Handle Cancel button click
            }

            val dialog = builder.create()
            dialog.show()
        }

        button_leavingHome.setOnClickListener{

            customDialog = CustomDialog(requireContext())

            customDialog!!.show()

            val handler = Handler()
            var index = 0


            handler.postDelayed(object : Runnable {
                override fun run() {
                    if (index < device_to_be_closed.size) {
                        val device = device_to_be_closed[index]
                        val requestParam = RequestParam(
                            HouseActivity.houseNumber,
                            "mode",
                            device,
                            "off",
                            "",
                            ""
                        )
                        customDialog!!.tvLoadingTx.text = "关闭"+device+".."
                        MqttService.publishMode(requestParam)
                        index++
                        handler.postDelayed(this, 1000) // 每隔2秒发送一次消息
                    } else {
                        customDialog!!.dismiss()
                    }
                }
            }, 2000) // 延迟2秒后开始发送消息
        }
        button_home.setOnClickListener{

            customDialog = CustomDialog(requireContext())

            customDialog!!.show()

            val handler = Handler()
            var index = 0


            handler.postDelayed(object : Runnable {
                override fun run() {
                    if (index < device_to_be_opened.size) {
                        val device = device_to_be_opened[index]
                        val requestParam = RequestParam(
                            HouseActivity.houseNumber,
                            "mode",
                            device,
                            "on",
                            "",
                            ""
                        )
                        customDialog!!.tvLoadingTx.text = "打开"+device+".."
                        MqttService.publishMode(requestParam)
                        index++
                        handler.postDelayed(this, 2000) // 每隔2秒发送一次消息
                    } else {
                        customDialog!!.dismiss()
                    }
                }
            }, 2000) // 延迟2秒后开始发送消息

        }

        getWeather()
        val iv = binding.weather
        iv.setOnClickListener { getWeather() }

        return root
    }
    private fun getWeather() {
        val city = URLEncoder.encode("漳州", "utf-8")
        val url =
            URL("https://api.seniverse.com/v3/weather/now.json?key=SWooXYSylwZkCP1wm&language=zh-Hans&unit=c&location=$city")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        val `in`: InputStream = connection.inputStream
        val reader = BufferedReader(InputStreamReader(`in`))
        val response = StringBuilder()
        var line: String?
        while ((reader.readLine().also { line = it }) != null) {
            response.append(line)
        }

        val res = String(response)
        Log.d("SmartHouse", res)
        try {
            val root = JSONObject(res)
            val arr = root.getJSONArray("results")[0] as JSONObject
            val code = arr.getJSONObject("now").getString("code")
            val iv = binding.weather
            when (code) {
                "0" -> iv.setImageResource(R.drawable.sunny)
                "1" -> iv.setImageResource(R.drawable.sunny_night)
                "2" -> iv.setImageResource(R.drawable.sunny)
                "3" -> iv.setImageResource(R.drawable.sunny_night)
                "4" -> iv.setImageResource(R.drawable.cloudy)
                "5" -> iv.setImageResource(R.drawable.partlycloudy)
                "6" -> iv.setImageResource(R.drawable.cloudy_night)
                "7" -> iv.setImageResource(R.drawable.partlycloudy)
                "8" -> iv.setImageResource(R.drawable.cloudy_night)
                "9" -> iv.setImageResource(R.drawable.overcast)
                "10" -> iv.setImageResource(R.drawable.showerrain)
                "11" -> iv.setImageResource(R.drawable.thundershower)
                "12" -> iv.setImageResource(R.drawable.thundershower_haze)
                "13" -> iv.setImageResource(R.drawable.lightrain)
                "14" -> iv.setImageResource(R.drawable.moderaterain)
                "15" -> iv.setImageResource(R.drawable.heavyrain)
                "16" -> iv.setImageResource(R.drawable.storm)
                "17" -> iv.setImageResource(R.drawable.storm)
                "18" -> iv.setImageResource(R.drawable.storm)
                "19" -> iv.setImageResource(R.drawable.icerain)
                "20" -> iv.setImageResource(R.drawable.sleet)
                "21" -> iv.setImageResource(R.drawable.snowflurry)
                "22" -> iv.setImageResource(R.drawable.lightsnow)
                "23" -> iv.setImageResource(R.drawable.moderatesnow)
                "24" -> iv.setImageResource(R.drawable.heavysnow)
                "25" -> iv.setImageResource(R.drawable.snowstorm)
                "26" -> iv.setImageResource(R.drawable.sand)
                "27" -> iv.setImageResource(R.drawable.sand)
                "28" -> iv.setImageResource(R.drawable.duststorm)
                "29" -> iv.setImageResource(R.drawable.duststorm)
                "30" -> iv.setImageResource(R.drawable.foggy)
                "31" -> iv.setImageResource(R.drawable.haze)
                "32" -> iv.setImageResource(R.drawable.windy)
                "33" -> iv.setImageResource(R.drawable.windy)
                "34" -> iv.setImageResource(R.drawable.hurricane)
                "35" -> iv.setImageResource(R.drawable.hurricane)
                "36" -> iv.setImageResource(R.drawable.tornado)
                "37" -> iv.setImageResource(R.drawable.cold)
                "38" -> iv.setImageResource(R.drawable.sunny)
                "99" -> iv.setImageResource(R.drawable.unknown)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
