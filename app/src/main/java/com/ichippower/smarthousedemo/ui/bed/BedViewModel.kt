package com.ichippower.smarthousedemo.ui.bed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ichippower.smarthousedemo.mqtt.ResponseParam

class BedViewModel : ViewModel() {

    private val _temperature_text = MutableLiveData<String>().apply {
        value = "28℃"
    }
    private val _humid_text = MutableLiveData<String>().apply {
        value = "40%"
    }
    private val _illumination_text = MutableLiveData<String>().apply {
        value = "900 Lux"
    }
    private val _pressure_text = MutableLiveData<String>().apply {
        value = "1 MPa"
    }

    var temperature_text: LiveData<String> = _temperature_text
    var humid_text: LiveData<String> = _humid_text
    var illumination_text: LiveData<String> = _illumination_text
    var pressure_text: LiveData<String> = _pressure_text

    fun update(data: ResponseParam) {
        temperature_text = MutableLiveData<String>().apply {
            value = "${data.getTemperature()}℃"
        }

        humid_text = MutableLiveData<String>().apply {
            value = "${data.getHumidity()}%"
        }

        illumination_text = MutableLiveData<String>().apply {
            value = "${data.getIllumination()} Lux"
        }

        pressure_text = MutableLiveData<String>().apply {
            value = "${data.getPressure()} MPa"
        }
    }
}