package com.ichippower.smarthousedemo.ui.bed

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
import com.ichippower.smarthousedemo.databinding.FragmentBedBinding
import com.ichippower.smarthousedemo.mqtt.MqttService
import com.ichippower.smarthousedemo.mqtt.ResponseParam

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


        val temperatureView: TextView = binding.temperatureText
        bedViewModel.temperature_text.observe(viewLifecycleOwner) {
            temperatureView.text = it
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