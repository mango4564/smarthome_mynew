package com.ichippower.smarthouse.ui.bed

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.ichippower.smarthouse.databinding.FragmentBedBinding
import com.ichippower.smarthouse.mqtt.MqttService
import com.ichippower.smarthouse.mqtt.RequestParam
import com.ichippower.smarthouse.mqtt.ResponseParam
import com.ichippower.smarthouse.ui.HouseActivity

class BedFragment : Fragment() {

    private var _binding: FragmentBedBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bedViewModel = ViewModelProvider(this)[BedViewModel::class.java]

        _binding = FragmentBedBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val lightButton = binding.switchBedLight
        lightButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(
                                HouseActivity.houseNumber,
                                "mode",
                                "卧室的灯",
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
                                "卧室的灯",
                                "off",
                                "",
                                ""
                            )
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }
        }

        val temperatureView: TextView = binding.temperatureText
        bedViewModel.temperature_text.observe(viewLifecycleOwner) {
            temperatureView.text = it
        }

        val humidView: TextView = binding.humidText
        bedViewModel.humid_text.observe(viewLifecycleOwner) {
            humidView.text = it
        }

        val illuminationView: TextView = binding.illuminationText
        bedViewModel.illumination_text.observe(viewLifecycleOwner) {
            illuminationView.text = it
        }

        val pressureView: TextView = binding.pressureText
        bedViewModel.pressure_text.observe(viewLifecycleOwner) {
            pressureView.text = it
        }

        val environmentView: LinearLayout = binding.environment
        environmentView.setOnClickListener {
            Thread {
                run {
                    MqttService.publishData()
                }
            }.start()
        }

        val myReceiver = MyReceiver(binding, bedViewModel)
        val intentFilter = IntentFilter()
        intentFilter.addAction(MqttService.ACTION_NAME)
        //注册广播
        context?.let {
            ContextCompat.registerReceiver(
                it, myReceiver, intentFilter,
                ContextCompat.RECEIVER_VISIBLE_TO_INSTANT_APPS
            )
        }

        return root
    }

    override fun onStart() {
        super.onStart()
        Thread {
            run {
                MqttService.publishData()
            }
        }.start()
    }

    class MyReceiver(
        private val binding: FragmentBedBinding,
        private val viewModel: BedViewModel
    ) : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent?) {
            (context as Activity).runOnUiThread {
                //获取从Service中传来的data
                if (intent != null) {
                    val message = intent.getStringExtra("data")
                    val data = Gson().fromJson(message, ResponseParam::class.java)
                    Log.d(BedFragment::class.java.simpleName, data.toString())
                    //更新UI
                    binding.temperatureText.text = data.getTemperature() + "℃"
                    binding.humidText.text = data.getHumidity() + "%"
                    binding.illuminationText.text = data.getIllumination() + " Lux"
                    binding.pressureText.text = data.getPressure() + " kPa"
                    viewModel.update(data)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}