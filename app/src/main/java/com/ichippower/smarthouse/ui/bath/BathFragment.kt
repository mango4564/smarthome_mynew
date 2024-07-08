package com.ichippower.smarthouse.ui.bath

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ichippower.smarthouse.databinding.FragmentBathBinding
import com.ichippower.smarthouse.mqtt.MqttService
import com.ichippower.smarthouse.mqtt.RequestParam
import com.ichippower.smarthouse.ui.HouseActivity

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

        val yubaButton = binding.switchYuba
        yubaButton.setOnCheckedChangeListener { _, isChecked ->
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

        val fanButton = binding.switchFan
        fanButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(HouseActivity.houseNumber, "mode", "排气扇", "on", "", "")
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            } else {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(HouseActivity.houseNumber, "mode", "排气扇", "off", "", "")
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }
        }

        val lightButton = binding.switchBathLight
        lightButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(
                                HouseActivity.houseNumber,
                                "mode",
                                "浴室的灯",
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
                                "浴室的灯",
                                "off",
                                "",
                                ""
                            )
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }
        }

        val curtainButton = binding.switchCurtain
        curtainButton.setOnCheckedChangeListener { _, isChecked ->
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

        val autoOffButton = binding.switchAutoOff
        autoOffButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(
                                HouseActivity.houseNumber,
                                "mode",
                                "自动打开窗帘",
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
                                "自动打开窗帘",
                                "off",
                                "",
                                ""
                            )
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }
        }

        val autoOnButton = binding.switchAutoOn
        autoOnButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Thread {
                    run {
                        val requestParam =
                            RequestParam(
                                HouseActivity.houseNumber,
                                "mode",
                                "自动关闭窗帘",
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
                                "自动关闭窗帘",
                                "off",
                                "",
                                ""
                            )
                        MqttService.publishMode(requestParam)
                    }
                }.start()
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}