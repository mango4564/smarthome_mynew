package com.ichippower.smarthousedemo.ui.kitchen

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
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.ichippower.smarthousedemo.databinding.FragmentKitchenBinding
import com.ichippower.smarthousedemo.mqtt.MqttService
import com.ichippower.smarthousedemo.mqtt.RequestParam
import com.ichippower.smarthousedemo.mqtt.ResponseParam
import com.ichippower.smarthousedemo.ui.HouseActivity

class KitchenFragment : Fragment() {

    private var _binding: FragmentKitchenBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val kitchenViewModel = ViewModelProvider(this)[KitchenViewModel::class.java]

        _binding = FragmentKitchenBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.COText
        kitchenViewModel.CO_text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        textView.setOnClickListener {
            Thread {
                run {
                    MqttService.publishData()
                }
            }.start()
        }

        val gasAlarmButton = binding.switchGasAlarm
        gasAlarmButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(
                                HouseActivity.houseNumber,
                                "mode",
                                "燃气报警",
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
                                "燃气报警",
                                "off",
                                "",
                                ""
                            )
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }
        }

        val myReceiver = MyReceiver(binding.COText, kitchenViewModel)
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
        private val textView: TextView,
        private val viewModel: KitchenViewModel
    ) : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent?) {
            (context as Activity).runOnUiThread {
                //获取从Service中传来的data
                if (intent != null) {
                    val message = intent.getStringExtra("data")
                    val data = Gson().fromJson(message, ResponseParam::class.java)
                    Log.d(KitchenFragment::class.java.simpleName, data.toString())
                    //更新UI
                    textView.text = data.getConcentration() + " PPM"
                    viewModel.update(data.getConcentration())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}