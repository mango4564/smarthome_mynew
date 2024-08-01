package com.ichippower.smarthousedemo.ui.living

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LivingViewModel : ViewModel() {
    var noise_text: LiveData<String> = MutableLiveData<String>().apply {
        value = "30 DB"
    }

    fun update(noise: String) {
        noise_text = MutableLiveData<String>().apply {
            value = "$noise DB"
        }
    }
}