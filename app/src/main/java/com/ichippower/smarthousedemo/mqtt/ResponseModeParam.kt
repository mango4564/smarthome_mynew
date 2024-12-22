package com.ichippower.smarthousedemo.mqtt

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
data class ResponseModeParam(
    private var houseNumber: String,
    //客厅
    private var switchDoor: String,
    private var switchLivingLight: String,
    private var switchLightRoof: String,

    //卧室
    private var switchBedLight: String,
    //厨房
    private var switchGasAlarm: String,
    private var switchKichenLight: String,
    private var switchKitchenFan: String,
    private var switchFireAlarm: String,
    //浴室
    private var switchHeater: String,
    private var switchWindow: String,
    private var switchYuba: String,
    private var switchBathroomFan: String,
    private var switchBathRoomLight: String,
    private var switchCurtain: String


) {
    fun getDevide(): String{
        if (switchDoor.isNotEmpty()) {
            return "入户门"
        }
        if (switchLivingLight.isNotEmpty()) {
            return "客厅的灯"
        }
        if (switchLightRoof.isNotEmpty()) {
            return "房梁的灯"
        }
        if (switchBedLight.isNotEmpty()) {
            return "卧室的灯"
        }
        if (switchGasAlarm.isNotEmpty()) {
            return "燃气报警"
        }
        if (switchKichenLight.isNotEmpty()) {
            return "厨房的灯"
        }
        if (switchKitchenFan.isNotEmpty()) {
            return "抽油烟机"
        }
        if (switchFireAlarm.isNotEmpty()) {
            return "火焰报警"
        }
        if (switchHeater.isNotEmpty()) {
            return "热水器"
        }
        if (switchWindow.isNotEmpty()) {
            return "窗户"
        }
        if (switchYuba.isNotEmpty()) {
            return "浴室的浴霸"
        }
        if (switchBathroomFan.isNotEmpty()) {
            return "浴室的排气扇"
        }
        if (switchBathRoomLight.isNotEmpty()) {
            return "浴室的灯"
        }
        if (switchCurtain.isNotEmpty()) {
            return "窗帘"
        }

        return "找不到相关设备"
    }
    fun getHouseNumber(): String {
        return houseNumber
    }

    fun getSwitchDoor(): String {
        return switchDoor
    }

    fun getSwitchLivingLight(): String {
        return switchLivingLight
    }

    fun getSwitchLightRoof(): String {
        return switchLightRoof
    }

    fun getSwitchBedLight(): String {
        return switchBedLight
    }

    fun getSwitchGasAlarm(): String {
        return switchGasAlarm
    }


    fun getSwitchKichenLight(): String {
        return switchKichenLight
    }

    fun getSwitchKitchenFan(): String {
        return switchKitchenFan
    }

    fun getSwitchFireAlarm(): String {
        return switchFireAlarm
    }

    fun getSwitchHeater(): String {
        return switchHeater
    }

    fun getSwitchWindow(): String {
        return switchWindow
    }

    fun getSwitchYuba(): String {
        return switchYuba
    }

    fun getSwitchFan(): String {
        return switchBathroomFan
    }

    fun getSwitchBathRoomLight(): String {
        return switchBathRoomLight
    }

    fun getSwitchCurtain(): String {
        return switchCurtain
    }




}