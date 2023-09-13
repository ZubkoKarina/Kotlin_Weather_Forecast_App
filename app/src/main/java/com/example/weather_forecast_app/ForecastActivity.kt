package com.example.weather_forecast_app
import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_forecast_app.adapter.ForeCastAdapter
import com.example.weather_forecast_app.mvvm.WeatherVm
@Suppress("DEPRECATION")
class ForecastActivity : AppCompatActivity() {
    private lateinit var adapterForeCastAdapter: ForeCastAdapter
    lateinit var viM : WeatherVm
    lateinit var rvForeCast: RecyclerView
    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fourdayforecast)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val attributes = window.attributes
            attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = attributes
        }
        viM = ViewModelProvider(this).get(WeatherVm::class.java)
        adapterForeCastAdapter = ForeCastAdapter()
        rvForeCast = findViewById<RecyclerView>(R.id.rvForeCast)
        val sharedPrefs = SharedPrefs.getInstance(this)
        val city = sharedPrefs.getValueOrNull("city")
        Log.d("Prefs", city.toString())
        if (city != null) {
            viM.getForecastUpcoming(city)
        } else {
            viM.getForecastUpcoming()
        }
        viM.forecastWeatherLiveData.observe(this, Observer {
            val setNewlist = it as List<WeatherList>
            Log.d("Forecast LiveData", setNewlist.toString())
            adapterForeCastAdapter.setList(setNewlist)
            rvForeCast.adapter = adapterForeCastAdapter
        })
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 != null) {
                    val deltaX = e2.x - e1.x
                    if (deltaX > 200) {
                        finish()
                        return true
                    }
                }
                return false
            }
        })
        rvForeCast.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }
    }
}