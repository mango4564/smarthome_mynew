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
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.ichippower.smarthousedemo.R
import com.ichippower.smarthousedemo.databinding.FragmentBedBinding
import com.ichippower.smarthousedemo.mqtt.MqttService
import com.ichippower.smarthousedemo.mqtt.RequestParam
import com.ichippower.smarthousedemo.mqtt.ResponseModeParam
import com.ichippower.smarthousedemo.mqtt.ResponseParam
import com.ichippower.smarthousedemo.ui.HouseActivity
import com.ichippower.smarthousedemo.ui.living.LivingFragment

class BedFragment : Fragment() {

    private var _binding: FragmentBedBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var isListenerEnabled = true
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bedViewModel = ViewModelProvider(this)[BedViewModel::class.java]

        _binding = FragmentBedBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val lightBedButton = binding.switchBedLight
        lightBedButton.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked&&isListenerEnabled) {
                requireContext().getSharedPreferences("houseStatus"+ HouseActivity.houseNumber, MODE_PRIVATE).edit{
                    putString("switchBedLight","*on")
                }
                Thread {
                    run {
                        val requestParam =
                            RequestParam(HouseActivity.houseNumber, "mode", "卧室的灯", "on", "", "")
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            } else {
                requireContext().getSharedPreferences("houseStatus"+ HouseActivity.houseNumber, MODE_PRIVATE).edit{
                    putString("switchBedLight","*off")
                }
                Thread {
                    run {
                        val requestParam =
                            RequestParam(HouseActivity.houseNumber, "mode", "卧室的灯", "off", "", "")
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }
        }

        val temperatureView: TextView = binding.temperatureText
        bedViewModel.temperature_text.observe(viewLifecycleOwner) {
            temperatureView.text = it
        }
        val humidityView: TextView = binding.humidityText
        bedViewModel.humid_text.observe(viewLifecycleOwner) {
            humidityView.text = it
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
                    if (message != null) {
                        if("houseNumber" in message){
//                            val data = Gson().fromJson(message, ResponseModeParam::class.java)
//                            Log.d(LivingFragment::class.java.simpleName, data.toString())
//                            //更新UI
//                            if(data.getDevice()=="卧室的灯") {
//                                if (data.getSwitch() == "on") {
//                                    binding.switchBedLight.isChecked = true
//                                } else {
//                                    binding.switchBedLight.isChecked = false
//                                }
//                            }
                        }else{
                            val data = Gson().fromJson(message, ResponseParam::class.java)
                            Log.d(BedFragment::class.java.simpleName, data.toString())
                            //更新UI
                            binding.temperatureText.text = data.getTemperature() + "℃"
                            binding.humidityText.text = data.getHumidity()+" %"
                            binding.illuminationText.text = data.getIllumination()+" Lux"
                            binding.pressureText.text = data.getPressure()+" Pa"
                            viewModel.update(data)
                        }
                    }
                }
            }
        }
    }
    private fun flashStatus(){
        val prefs = requireContext().getSharedPreferences("houseStatus"+HouseActivity.houseNumber, Context.MODE_PRIVATE)
        val switchBedLight=prefs.getString("switchBedLight","")
        if(switchBedLight !=null){
            if(switchBedLight.isNotEmpty()){
                if("*on" in switchBedLight){
                    binding.switchBedLight.isChecked=true
                }else if("*off" in switchBedLight){
                    binding.switchBedLight.isChecked=false
                }
            }
        }
    }
    override fun onResume() {
        flashStatus()
        isListenerEnabled = true
        super.onResume()
    }
    override fun onPause() {
        super.onPause()
        isListenerEnabled = false
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}