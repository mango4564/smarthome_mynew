package com.ichippower.smarthouse.ui.living

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.ichippower.smarthouse.R
import com.ichippower.smarthouse.databinding.FragmentLivingBinding
import com.ichippower.smarthouse.mqtt.MqttService
import com.ichippower.smarthouse.mqtt.RequestParam
import com.ichippower.smarthouse.mqtt.ResponseParam
import com.ichippower.smarthouse.ui.HouseActivity
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class LivingFragment : Fragment() {

    private var _binding: FragmentLivingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val livingViewModel = ViewModelProvider(this)[LivingViewModel::class.java]
        _binding = FragmentLivingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val noiseView: TextView = binding.noiseText
        livingViewModel.noise_text.observe(viewLifecycleOwner) {
            noiseView.text = it
        }
        noiseView.setOnClickListener {
            Thread {
                run {
                    MqttService.publishData()
                }
            }.start()
        }

        // 当前使用Toast作为按钮反馈，需要替换为MQTT操作
        val doorButton = binding.switchDoor
        doorButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(
                                HouseActivity.houseNumber,
                                "mode",
                                "客厅的门",
                                "on",
                                "",
                                ""
                            )
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            } else {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(
                                HouseActivity.houseNumber,
                                "mode",
                                "客厅的门",
                                "off",
                                "",
                                ""
                            )
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }
        }

        val bandButton = binding.switchLightBand
        bandButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(HouseActivity.houseNumber, "mode", "灯带", "on", "", "")
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            } else {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(HouseActivity.houseNumber, "mode", "灯带", "off", "", "")
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }
        }

        val lightButton = binding.switchLivingLight
        val radioGroupColor = binding.colorGroup
        val radioGroupMode = binding.modeGroup
        lightButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableRadioGroup(radioGroupColor)
                enableRadioGroup(radioGroupMode)
                Thread {
                    run {
                        val requestParam =
                            RequestParam(
                                HouseActivity.houseNumber,
                                "mode",
                                "客厅的灯",
                                "on",
                                "yellow",
                                "模式一"
                            )
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            } else {
                disableRadioGroup(radioGroupColor)
                disableRadioGroup(radioGroupMode)
                Thread {
                    run {
                        val requestParam =
                            RequestParam(
                                HouseActivity.houseNumber,
                                "mode",
                                "客厅的灯",
                                "off",
                                "",
                                ""
                            )
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }
        }

        radioGroupColor.setOnCheckedChangeListener { _, _ -> selectColor() }
        disableRadioGroup(radioGroupColor)

        radioGroupMode.setOnCheckedChangeListener { _, _ -> selectMode() }
        disableRadioGroup(radioGroupMode)

        val iv = binding.weather
        iv.setOnClickListener { getWeather() }

        val myReceiver = MyReceiver(binding.noiseText, livingViewModel)
        val intentFilter = IntentFilter()
        intentFilter.addAction(MqttService.ACTION_NAME)
        //注册广播
        context?.let {
            registerReceiver(
                it, myReceiver, intentFilter,
                ContextCompat.RECEIVER_VISIBLE_TO_INSTANT_APPS
            )
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        getWeather()
    }

    override fun onStart() {
        super.onStart()
        Thread {
            run {
                MqttService.publishData()
            }
        }.start()
    }

    private fun selectColor() {
        val rb: RadioButton = binding.root.findViewById(binding.colorGroup.checkedRadioButtonId)
        val iv = binding.livingLight
        when (rb.tag) {
            "1" -> {
                iv.setImageResource(R.drawable.yellow_light)
                Thread {
                    run {
                        val requestParam =
                            RequestParam(
                                HouseActivity.houseNumber,
                                "mode",
                                "客厅的灯",
                                "on",
                                "yellow",
                                ""
                            )
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }

            "2" -> {
                iv.setImageResource(R.drawable.blue_light)
                Thread {
                    run {
                        val requestParam =
                            RequestParam(
                                HouseActivity.houseNumber,
                                "mode",
                                "客厅的灯",
                                "on",
                                "blue",
                                ""
                            )
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }

            "3" -> {
                iv.setImageResource(R.drawable.red_light)
                Thread {
                    run {
                        val requestParam =
                            RequestParam(
                                HouseActivity.houseNumber,
                                "mode",
                                "客厅的灯",
                                "on",
                                "red",
                                ""
                            )
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }

            "4" -> {
                iv.setImageResource(R.drawable.purple_light)
                Thread {
                    run {
                        val requestParam =
                            RequestParam(
                                HouseActivity.houseNumber,
                                "mode",
                                "客厅的灯",
                                "on",
                                "purple",
                                ""
                            )
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }
        }
    }

    private fun selectMode() {
        val rb: RadioButton = binding.root.findViewById(binding.modeGroup.checkedRadioButtonId)
        when (rb.tag) {
            "1" -> {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(
                                HouseActivity.houseNumber,
                                "mode",
                                "客厅的灯",
                                "on",
                                "",
                                "模式一"
                            )
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }

            "2" -> {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(
                                HouseActivity.houseNumber,
                                "mode",
                                "客厅的灯",
                                "on",
                                "",
                                "模式二"
                            )
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }

            "3" -> {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(
                                HouseActivity.houseNumber,
                                "mode",
                                "客厅的灯",
                                "on",
                                "",
                                "模式三"
                            )
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }
        }
    }

    private fun getWeather() {
        val city = URLEncoder.encode("厦门", "utf-8")
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

    class MyReceiver(
        private val textView: TextView,
        private val viewModel: LivingViewModel
    ) : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent?) {
            (context as Activity).runOnUiThread {
                //获取从Service中传来的data
                if (intent != null) {
                    val message = intent.getStringExtra("data")
                    val data = Gson().fromJson(message, ResponseParam::class.java)
                    Log.d(LivingFragment::class.java.simpleName, data.toString())
                    //更新UI
                    textView.text = data.getNoise() + " DB"
                    viewModel.update(data.getNoise())
                }
            }
        }
    }

    /**
     * 启用RadioGroup
     * @param radioGroup
     */
    private fun enableRadioGroup(radioGroup: RadioGroup) {
        for (i in 0 until radioGroup.childCount) {
            radioGroup.getChildAt(i).isEnabled = true
            (radioGroup.getChildAt(i) as RadioButton).isChecked = false
        }
    }

    /**
     * 禁用RadioGroup
     * @param radioGroup
     */
    private fun disableRadioGroup(radioGroup: RadioGroup) {
        for (i in 0 until radioGroup.childCount) {
            radioGroup.getChildAt(i).isEnabled = false
            (radioGroup.getChildAt(i) as RadioButton).isChecked = false
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}