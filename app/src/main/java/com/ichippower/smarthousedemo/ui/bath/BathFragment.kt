package com.ichippower.smarthousedemo.ui.bath

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.ichippower.smarthousedemo.R
import com.ichippower.smarthousedemo.databinding.FragmentBathBinding
import com.ichippower.smarthousedemo.mqtt.MqttService
import com.ichippower.smarthousedemo.mqtt.RequestParam
import com.ichippower.smarthousedemo.ui.HouseActivity
import java.util.Calendar

class BathFragment : Fragment() {

    private var _binding: FragmentBathBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBathBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val heaterButton = binding.switchHeater
        heaterButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(HouseActivity.houseNumber, "mode", "热水器", "on", "", "")
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            } else {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(HouseActivity.houseNumber, "mode", "热水器", "off", "", "")
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }
        }

        val windowButton = binding.switchWindow
        windowButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(HouseActivity.houseNumber, "mode", "窗户", "on", "", "")
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            } else {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(HouseActivity.houseNumber, "mode", "窗户", "off", "", "")
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }
        }

//      新添加的
        val yubaButton=binding.switchYuba
        yubaButton.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(HouseActivity.houseNumber, "mode", "浴霸", "on", "", "")
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            } else {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(HouseActivity.houseNumber, "mode", "浴霸", "off", "", "")
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }
        }

        val fanButton=binding.switchFan
        val radioGroupSpeed = binding.speedGroup
        fanButton.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                enableRadioGroup(radioGroupSpeed)
                Thread {
                    run {
                        val requestParam =
                            RequestParam(HouseActivity.houseNumber, "mode", "排气扇", "on", "", "低速")
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            } else {
                disableRadioGroup(radioGroupSpeed)
                Thread {
                    run {
                        val requestParam =
                            RequestParam(HouseActivity.houseNumber, "mode", "排气扇", "off", "", "")
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }
        }
        disableRadioGroup(radioGroupSpeed)
        radioGroupSpeed.setOnCheckedChangeListener { _, _ -> selectSpeed() }

        val bathRoomLight=binding.switchBathRoomLight
        bathRoomLight.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(HouseActivity.houseNumber, "mode", "浴室的灯", "on", "", "")
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            } else {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(HouseActivity.houseNumber, "mode", "浴室的灯", "off", "", "")
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }
        }

        val curtainButton=binding.switchCurtain
        curtainButton.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(HouseActivity.houseNumber, "mode", "窗帘", "on", "", "")
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            } else {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(HouseActivity.houseNumber, "mode", "窗帘", "off", "", "")
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }
        }



        return root
    }

    private fun selectSpeed() {
        val rb: RadioButton = binding.root.findViewById(binding.speedGroup.checkedRadioButtonId)
        //val iv = binding.livingLight
        when (rb.tag) {
            "1" -> {
                //iv.setImageResource(R.drawable.yellow_light)
                Thread {
                    run {
                        val requestParam =
                            RequestParam(
                                HouseActivity.houseNumber,
                                "mode",
                                "排气扇",
                                "on",
                                "",
                                "低速"
                            )
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }

            "2" -> {
                //iv.setImageResource(R.drawable.blue_light)
                Thread {
                    run {
                        val requestParam =
                            RequestParam(
                                HouseActivity.houseNumber,
                                "mode",
                                "排气扇",
                                "on",
                                "",
                                "中速"
                            )
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }

            "3" -> {
                //iv.setImageResource(R.drawable.red_light)
                Thread {
                    run {
                        val requestParam =
                            RequestParam(
                                HouseActivity.houseNumber,
                                "mode",
                                "排气扇",
                                "on",
                                "",
                                "高速"
                            )
                        MqttService.publishMode(requestParam)
                    }
                }.start()
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