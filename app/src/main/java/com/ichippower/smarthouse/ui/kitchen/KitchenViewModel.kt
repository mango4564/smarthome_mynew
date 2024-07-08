package com.ichippower.smarthouse.ui.kitchen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class KitchenViewModel : ViewModel() {

    private val _CO_text = MutableLiveData<String>().apply {
        value = "8 PPM"
    }
    var CO_text: LiveData<String> = _CO_text

    fun update(concentration: String) {
        CO_text = MutableLiveData<String>().apply {
            value = "$concentration PPM"
        }
    }
}