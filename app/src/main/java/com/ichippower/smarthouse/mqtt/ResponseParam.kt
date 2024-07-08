package com.ichippower.smarthouse.mqtt

import kotlinx.serialization.Serializable

/**
 * temperature：温度
 * humidity：湿度
 * illumination：光照强度
 * pressure：气压
 * noise：噪声强度
 * concentration：一氧化碳浓度
 * alarm：是否报警，在发送数据时设置为false
 * alarmType：报警类型，火焰报警/燃气报警，alarm为false时无效
 */
@Serializable
data class ResponseParam(
    private var temperature: String,
    private var humidity: String,
    private var illumination: String,
    private var pressure: String,
    private var noise: String,
    private var concentration: String,
    private var alarm: String,
    private var alarmType: String
) {
    fun getTemperature(): String {
        return temperature
    }

    fun getHumidity(): String {
        return humidity
    }

    fun getIllumination(): String {
        return illumination
    }

    fun getPressure(): String {
        return pressure
    }

    fun getNoise(): String {
        return noise
    }

    fun getConcentration(): String {
        return concentration
    }

    fun getAlarm(): String {
        return alarm
    }

    fun getAlarmType(): String {
        return alarmType
    }
}