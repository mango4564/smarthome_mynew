package com.ichippower.smarthousedemo.utils

import android.util.Log
import android.widget.ImageView
import com.ichippower.smarthousedemo.R
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object WeatherUtils {
    fun getWeather(weatherImageView: ImageView) {
        val city = URLEncoder.encode("漳州", "utf-8")
        val url =
            URL("https://api.seniverse.com/v3/weather/now.json?key=SWooXYSylwZkCP1wm&language=zh-Hans&unit=c&location=$city")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        val `in`: InputStream = connection.inputStream
        val reader = BufferedReader(InputStreamReader(`in`))
        val response = StringBuilder()
        var line: String?
        while ((reader.readLine().also { line = it }) != null) {
            response.append(line)
        }

        val res = String(response)
        Log.d("SmartHouse", res)
        try {
            val root = JSONObject(res)
            val arr = root.getJSONArray("results")[0] as JSONObject
            val code = arr.getJSONObject("now").getString("code")
            when (code) {
                "0" -> weatherImageView.setImageResource(R.drawable.sunny)
                "1" -> weatherImageView.setImageResource(R.drawable.sunny_night)
                "2" -> weatherImageView.setImageResource(R.drawable.sunny)
                "3" -> weatherImageView.setImageResource(R.drawable.sunny_night)
                "4" -> weatherImageView.setImageResource(R.drawable.cloudy)
                "5" -> weatherImageView.setImageResource(R.drawable.partlycloudy)
                "6" -> weatherImageView.setImageResource(R.drawable.cloudy_night)
                "7" -> weatherImageView.setImageResource(R.drawable.partlycloudy)
                "8" -> weatherImageView.setImageResource(R.drawable.cloudy_night)
                "9" -> weatherImageView.setImageResource(R.drawable.overcast)
                "10" -> weatherImageView.setImageResource(R.drawable.showerrain)
                "11" -> weatherImageView.setImageResource(R.drawable.thundershower)
                "12" -> weatherImageView.setImageResource(R.drawable.thundershower_haze)
                "13" -> weatherImageView.setImageResource(R.drawable.lightrain)
                "14" -> weatherImageView.setImageResource(R.drawable.moderaterain)
                "15" -> weatherImageView.setImageResource(R.drawable.heavyrain)
                "16" -> weatherImageView.setImageResource(R.drawable.storm)
                "17" -> weatherImageView.setImageResource(R.drawable.storm)
                "18" -> weatherImageView.setImageResource(R.drawable.storm)
                "19" -> weatherImageView.setImageResource(R.drawable.icerain)
                "20" -> weatherImageView.setImageResource(R.drawable.sleet)
                "21" -> weatherImageView.setImageResource(R.drawable.snowflurry)
                "22" -> weatherImageView.setImageResource(R.drawable.lightsnow)
                "23" -> weatherImageView.setImageResource(R.drawable.moderatesnow)
                "24" -> weatherImageView.setImageResource(R.drawable.heavysnow)
                "25" -> weatherImageView.setImageResource(R.drawable.snowstorm)
                "26" -> weatherImageView.setImageResource(R.drawable.sand)
                "27" -> weatherImageView.setImageResource(R.drawable.sand)
                "28" -> weatherImageView.setImageResource(R.drawable.duststorm)
                "29" -> weatherImageView.setImageResource(R.drawable.duststorm)
                "30" -> weatherImageView.setImageResource(R.drawable.foggy)
                "31" -> weatherImageView.setImageResource(R.drawable.haze)
                "32" -> weatherImageView.setImageResource(R.drawable.windy)
                "33" -> weatherImageView.setImageResource(R.drawable.windy)
                "34" -> weatherImageView.setImageResource(R.drawable.hurricane)
                "35" -> weatherImageView.setImageResource(R.drawable.hurricane)
                "36" -> weatherImageView.setImageResource(R.drawable.tornado)
                "37" -> weatherImageView.setImageResource(R.drawable.cold)
                "38" -> weatherImageView.setImageResource(R.drawable.sunny)
                "99" -> weatherImageView.setImageResource(R.drawable.unknown)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
} 