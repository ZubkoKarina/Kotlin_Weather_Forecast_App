package com.example.weather_forecast_app

import android.app.Application

class MyApplication : Application()   {
    companion object{
        lateinit var instance : MyApplication
    }
    override fun onCreate(){
        super.onCreate()
        instance = this
    }
    override fun onTerminate() {
        super.onTerminate()
        val sharedPrefs = SharedPrefs.getInstance(this)
        sharedPrefs.clearCityValue()
    }
}