package com.ichippower.smarthousedemo.mqtt

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.content.edit
import com.google.gson.Gson
import com.ichippower.smarthousedemo.R
import com.ichippower.smarthousedemo.ui.HouseActivity
import info.mqtt.android.service.Ack
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.io.InterruptedIOException


class MqttService : Service() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        init()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /**
     * 响应 （收到其他客户端的消息后，处理数据）
     *
     * @param message 消息
     */
    fun response(message: String) {
        val mIntent = Intent().setAction(ACTION_NAME)
        //开启一个线程，对数据进行处理
        Thread {
            run {
                try {
                    //耗时操作：数据处理并保存
                    if("houseNumber" in message){
                        mIntent.putExtra("data", message)
                        sendBroadcast(mIntent)
                        val data = Gson().fromJson(message, ResponseModeParam::class.java)
                        getSharedPreferences("houseStatus"+ HouseActivity.houseNumber, MODE_PRIVATE).edit{
                            clear()
                            putString("switchDoor", data.getSwitchDoor())
                            putString("switchLivingLight",data.getSwitchLivingLight())
                            putString("switchLightRoof", data.getSwitchLightRoof())
                            putString("switchBedLight", data.getSwitchBedLight())
                            putString("switchGasAlarm", data.getSwitchGasAlarm())
                            putString("switchKichenLight", data.getSwitchKichenLight())
                            putString("switchKitchenFan", data.getSwitchKitchenFan())
                            putString("switchFireAlarm", data.getSwitchFireAlarm())
                            putString("switchHeater", data.getSwitchHeater())
                            putString("switchWindow", data.getSwitchWindow())
                            putString("switchYuba", data.getSwitchYuba())
                            putString("switchBathroomFan", data.getSwitchFan())
                            putString("switchBathRoomLight", data.getSwitchBathRoomLight())
                            putString("switchCurtain", data.getSwitchCurtain())
                        }

                    }else{
                        mIntent.putExtra("data", message)
                        sendBroadcast(mIntent)
                        checkAlarm(message)
                        Thread.sleep(300)
                    }

                } catch (e: InterruptedIOException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    /**
     * 响应警报 （收到其他客户端的消息后，处理警报）
     *
     * @param message 消息
     */
    private fun checkAlarm(message: String) {
        val data = Gson().fromJson(message, ResponseParam::class.java)
        val text: String
        if (data.getAlarm() == "true") {
            text = if (data.getAlarmType() == "火焰报警") {
                "家里起火了，请尽快处理！！！"
            } else if (data.getAlarmType() == "燃气报警") {
                "家里燃气泄露了，请尽快处理！！！"
            } else {
                "未知报警信息"
            }

            val context = applicationContext
            val notification: Notification = Notification.Builder(context, "smarthouse")
                .setContentTitle(data.getAlarmType())
                .setContentText(text)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_notifications_black_24dp)) //设置大图标
                .setAutoCancel(true)
                .build()
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channel =
                NotificationChannel("smarthouse", "smarthouse", NotificationManager.IMPORTANCE_HIGH)
            channel.setShowBadge(true)
            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(1, notification)

        }
    }

    /**
     * 初始化
     */
    private fun init() {
        mqttAndroidClient = MqttAndroidClient(
            this, HOST, CLIENTID, Ack.AUTO_ACK
        )
        mqttAndroidClient!!.setCallback(mqttCallback) //设置监听订阅消息的回调
        mMqttConnectOptions = MqttConnectOptions()
        mMqttConnectOptions!!.isCleanSession = true //设置是否清除缓存
        mMqttConnectOptions!!.connectionTimeout = 10 //设置超时时间，单位：秒
        mMqttConnectOptions!!.keepAliveInterval = 20 //设置心跳包发送间隔，单位：秒
        mMqttConnectOptions!!.userName = USERNAME //设置用户名
        mMqttConnectOptions!!.password = PASSWORD.toCharArray() //设置密码

        // last will message
        var doConnect = true
        val message = "{\"terminal_uid\":\"$CLIENTID\"}"
        val topic = subscribeTopic
        val qos = 2
        val retained = false
        // 最后的遗嘱
        try {
            mMqttConnectOptions!!.setWill(topic, message.toByteArray(), qos, retained)
        } catch (e: Exception) {
            Log.i(TAG, "Exception Occurred", e)
            doConnect = false
            iMqttActionListener.onFailure(null, e)
        }
        if (doConnect) {
            doClientConnection()
        }
    }

    /**
     * 连接MQTT服务器
     */
    private fun doClientConnection() {
        if (!mqttAndroidClient!!.isConnected && isConnectIsNormal) {
            mqttAndroidClient!!.connect(
                mMqttConnectOptions!!, null, iMqttActionListener
            )
        }
    }

    private val isConnectIsNormal: Boolean
        /**
         * 判断网络是否连接
         */
        get() {
            val connectivityManager =
                this.applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            if (network != null) {
                val nc = connectivityManager.getNetworkCapabilities(network)
                if (nc != null) {
                    val name =
                        if (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) "WIFI" else "MOBILE"
                    Log.i(TAG, "当前网络名称：$name")
                    return true
                } else {
                    Log.i(TAG, "没有可用网络")
                    /*没有可用网络的时候，延迟3秒再尝试重连*/
                    Handler(Looper.getMainLooper()).postDelayed({ this.doClientConnection() }, 3000)
                }
            } else {
                Log.i(TAG, "没有可用网络")
                /*没有可用网络的时候，延迟3秒再尝试重连*/
                Handler(Looper.getMainLooper()).postDelayed({ this.doClientConnection() }, 3000)
            }
            return false
        }

    //MQTT是否连接成功的监听
    private val iMqttActionListener: IMqttActionListener = object : IMqttActionListener {
        override fun onSuccess(arg0: IMqttToken) {
            Log.i(TAG, "连接成功 ")
            mqttAndroidClient!!.subscribe(subscribeTopic, 2) //订阅主题，参数：主题、服务质量
            Log.d(TAG, "subscribe to $subscribeTopic")
        }

        override fun onFailure(arg0: IMqttToken, arg1: Throwable) {
            arg1.printStackTrace()
            Log.i(TAG, "连接失败 ")
            doClientConnection() //连接失败，重连（可关闭服务器进行模拟）
        }
    }

    //订阅主题的回调
    private val mqttCallback: MqttCallback = object : MqttCallback {
        override fun messageArrived(topic: String, message: MqttMessage) {
            Log.i(TAG, "收到消息： " + String(message.payload)+"topic:"+topic)
            //收到消息后，处理数据
            response(String(message.payload))
        }

        override fun deliveryComplete(arg0: IMqttDeliveryToken) {}

        override fun connectionLost(arg0: Throwable) {
            Log.i(TAG, "连接断开 ")
            doClientConnection() //连接断开，重连
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "MQTT disconnect")
        if (mqttAndroidClient?.isConnected == true) {
            mqttAndroidClient!!.disconnect() //断开连接
        }
        super.onDestroy()
    }

    companion object {
        val TAG: String = MqttService::class.java.simpleName
        private var mMqttConnectOptions: MqttConnectOptions? = null

        @SuppressLint("StaticFieldLeak")
        private var mqttAndroidClient: MqttAndroidClient? = null
        private const val HOST = "tcp://192111111:1883" //服务器地址（协议+地址+端口号）
        private const val USERNAME = "mqtt:root" //用户名
        private const val PASSWORD = "xmxllAdmin2020." //密码
        lateinit var subscribeTopic: String //订阅主题
        lateinit var publishTopic: String //发布主题
        lateinit var CLIENTID: String //客户端ID，一般以客户端唯一标识符表示
        const val ACTION_NAME = "Update Data"

        /**
         * 开启服务
         */
        fun startService(mContext: Context) {
            val intent = Intent(mContext, MqttService::class.java)
            intent.putExtra("houseNumber",HouseActivity.houseNumber)
            mContext.startService(intent)
        }

        /**
         * 获取连接状态
         */
        fun getServiceConnStat(): Boolean {
            return if (mqttAndroidClient == null) {
                false
            } else {
                mqttAndroidClient!!.isConnected
            }
        }

        /**
         * 获取数据
         */
        fun publishData() {
            //等待客户端连接
            while (!mqttAndroidClient?.isConnected!!) {
                continue
            }
            val qos = 2
            val retained = false
            val request = RequestParam(HouseActivity.houseNumber, "data", "", "", "", "")
            val message = Gson().toJson(request)
            Log.d(TAG, "publish message: $message")
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            mqttAndroidClient!!.publish(publishTopic, message.toByteArray(), qos, retained)
        }

        /**
         * 修改模式
         *
         * @param requestParam 请求参数
         */
        fun publishMode(requestParam: RequestParam) {
            //等待客户端连接
            while (!mqttAndroidClient?.isConnected!!) {
                continue
            }
            val qos = 2
            val retained = false
            val message = Gson().toJson(requestParam)
            Log.d(TAG, "publish message: $message")
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            mqttAndroidClient!!.publish(publishTopic, message.toByteArray(), qos, retained)
        }

        /**
         * 订阅主题
         */
        fun subscribe() {
            //等待客户端连接
            while (!mqttAndroidClient?.isConnected!!) {
                continue
            }
            mqttAndroidClient!!.subscribe(subscribeTopic, 2) //订阅主题，参数：主题、服务质量
            Log.d(TAG, "subscribe to $subscribeTopic")
        }

        /**
         * 停止订阅主题
         */
        fun unsubscribe() {
            //等待客户端连接
            while (!mqttAndroidClient?.isConnected!!) {
                continue
            }
            mqttAndroidClient!!.unsubscribe(subscribeTopic)
            Log.d(TAG, "unsubscribe from $subscribeTopic")
        }
    }
}