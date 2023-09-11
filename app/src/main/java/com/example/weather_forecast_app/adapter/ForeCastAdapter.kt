package com.example.weather_forecast_app.adapter
import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.weather_forecast_app.R
import com.example.weather_forecast_app.WeatherList
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ForeCastAdapter : RecyclerView.Adapter<ForeCastHolder>() {
    private var listofforecast = listOf<WeatherList>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForeCastHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.fourdaylistitem, parent, false)
        return ForeCastHolder(view)
    }
    override fun getItemCount(): Int {
        return listofforecast.size
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ForeCastHolder, position: Int) {
        val forecastObject = listofforecast[position]
        for (i in forecastObject.weather){
            holder.description.text = i.description!!
        }
        holder.humiditiy.text = forecastObject.main!!.humidity.toString()
        holder.windspeed.text = forecastObject.wind?.speed.toString()
        val temperatureFahrenheit = forecastObject.main?.temp
        val temperatureCelsius = (temperatureFahrenheit?.minus(273.15))
        val temperatureFormatted = String.format("%.2f", temperatureCelsius)
        holder.temp.text = "$temperatureFormatted °C"
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = inputFormat.parse(forecastObject.dtTxt!!)
        val outputFormat = SimpleDateFormat("d MMMM EEEE", Locale.getDefault())
         val dateanddayname = outputFormat.format(date!!)
        holder.dateDayName.text = dateanddayname
        for (i in forecastObject.weather) {
            if (i.icon == "01d") {
                holder.imageGraphic.setImageResource(R.drawable.ic_sun)
                holder.smallIcon.setImageResource(R.drawable.ic_sun)
            }
            if (i.icon == "01n") {
                holder.imageGraphic.setImageResource(R.drawable.ic_moon)
                holder.smallIcon.setImageResource(R.drawable.ic_moon)
            }
            if (i.icon == "02d") {
                holder.imageGraphic.setImageResource(R.drawable.ic_cloudy)
                holder.smallIcon.setImageResource(R.drawable.ic_cloudy)
            }
            if (i.icon == "02n") {
                holder.imageGraphic.setImageResource(R.drawable.ic_night_moon_moon)
                holder.smallIcon.setImageResource(R.drawable.ic_night_moon_moon)
            }
            if (i.icon == "03d" || i.icon == "03n") {
                holder.imageGraphic.setImageResource(R.drawable.ic_cloud)
                holder.smallIcon.setImageResource(R.drawable.ic_cloud)
            }
            if (i.icon == "10d") {
                holder.imageGraphic.setImageResource(R.drawable.ic_rain_rain)
                holder.smallIcon.setImageResource(R.drawable.ic_rain_rain)
            }
            if (i.icon == "10n") {
                holder.imageGraphic.setImageResource(R.drawable.ic_rain_forecast)
                holder.smallIcon.setImageResource(R.drawable.ic_rain_forecast)
            }
            if (i.icon == "04d" || i.icon == "04n") {
                holder.imageGraphic.setImageResource(R.drawable.ic_cloudy_spring)
                holder.smallIcon.setImageResource(R.drawable.ic_cloudy_spring)
            }
            if (i.icon == "09d" || i.icon == "09n") {
                holder.imageGraphic.setImageResource(R.drawable.ic_rain)
                holder.smallIcon.setImageResource(R.drawable.ic_rain)
            }
            if (i.icon == "11d" || i.icon == "11n") {
                holder.imageGraphic.setImageResource(R.drawable.ic_storm)
                holder.smallIcon.setImageResource(R.drawable.ic_storm)
            }
            if (i.icon == "13d" || i.icon == "13n") {
                holder.imageGraphic.setImageResource(R.drawable.ic_snowflake)
                holder.smallIcon.setImageResource(R.drawable.ic_snowflake)
            }
            if (i.icon == "50d" || i.icon == "50n") {
                holder.imageGraphic.setImageResource(R.drawable.ic_tornado)
                holder.smallIcon.setImageResource(R.drawable.ic_tornado)
            }
        }
    }
    fun setList(newlist: List<WeatherList>) {
        this.listofforecast = newlist
    }
}
class ForeCastHolder(itemView: View) : ViewHolder(itemView){
    val imageGraphic: ImageView = itemView.findViewById(R.id.imageGraphic)
    val description : TextView = itemView.findViewById(R.id.weatherDescr)
    val humiditiy : TextView = itemView.findViewById(R.id.humidity)
    val windspeed : TextView = itemView.findViewById(R.id.windSpeed)
    val temp : TextView = itemView.findViewById(R.id.tempDisplayForeCast)
    val smallIcon : ImageView = itemView.findViewById(R.id.smallIcon)
    val dateDayName : TextView = itemView.findViewById(R.id.dayDateText)
}