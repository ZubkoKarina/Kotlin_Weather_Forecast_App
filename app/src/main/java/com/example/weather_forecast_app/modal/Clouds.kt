package com.example.weather_forecast_app
import com.google.gson.annotations.SerializedName
data class Clouds (
  @SerializedName("all" ) var all : Int? = null
)