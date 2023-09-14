package com.example.weather_forecast_app
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weather_forecast_app.adapter.WeatherToday
import com.example.weather_forecast_app.databinding.TestlayoutBinding
import com.example.weather_forecast_app.mvvm.WeatherVm
import java.text.SimpleDateFormat
import java.util.*
@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    lateinit var viM: WeatherVm
    lateinit var adapter: WeatherToday
    private lateinit var binding: TestlayoutBinding
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val attributes = window.attributes
            attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = attributes
        }
        binding = DataBindingUtil.setContentView(this, R.layout.testlayout)
        viM = ViewModelProvider(this).get(WeatherVm::class.java)
        viM.getWeather()
        adapter = WeatherToday()
        val sharedPrefs = SharedPrefs.getInstance(this@MainActivity)
        sharedPrefs.clearCityValue()
        viM.todayWeatherLiveData.observe(this, Observer {
            val setNewlist = it as List<WeatherList>
            adapter.setList(setNewlist)
            binding.forecastRecyclerView.adapter = adapter
        })
        binding.lifecycleOwner = this
        binding.mVm = viM
        viM.closetorexactlysameweatherdata.observe(this, Observer {
            val temperatureFahrenheit = it!!.main?.temp
            val temperatureCelsius = (temperatureFahrenheit?.minus(273.15))
            val temperatureFormatted = String.format("%.2f", temperatureCelsius)
            for (i in it.weather) {
                val description = i.description
                if (description != null && description.isNotEmpty()) {
                    val capitalizedDescription = description.substring(0, 1).toUpperCase(Locale.ROOT) + description.substring(1)
                    binding.descMain.text = capitalizedDescription
                } else {
                    binding.descMain.text = ""
                }
            }
            binding.tempMain.text = "$temperatureFormatted°"
            binding.humidityMain.text = it.main!!.humidity.toString()
            binding.windSpeed.text = it.wind?.speed.toString()
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = inputFormat.parse(it.dtTxt!!)
            val outputFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.ENGLISH)
            val dateanddayname = outputFormat.format(date!!)
            binding.dateDayMain.text = dateanddayname
            binding.chanceofrain.text = "${it.pop.toString()}%"
            for (i in it.weather) {
                if (i.icon == "01d") {
                    binding.imageMain.setImageResource(R.drawable.ic_sun)
                }
                if (i.icon == "01n") {
                    binding.imageMain.setImageResource(R.drawable.ic_moon)
                }
                if (i.icon == "02d") {
                    binding.imageMain.setImageResource(R.drawable.ic_cloudy)
                }
                if (i.icon == "02n") {
                    binding.imageMain.setImageResource(R.drawable.ic_night_moon_moon)
                }
                if (i.icon == "03d" || i.icon == "03n") {
                    binding.imageMain.setImageResource(R.drawable.ic_cloud)
                }
                if (i.icon == "10d") {
                    binding.imageMain.setImageResource(R.drawable.ic_rain_rain)
                }
                if (i.icon == "10n") {
                    binding.imageMain.setImageResource(R.drawable.ic_rain_forecast)
                }
                if (i.icon == "04d" || i.icon == "04n") {
                    binding.imageMain.setImageResource(R.drawable.ic_cloudy_spring)
                }
                if (i.icon == "09d" || i.icon == "09n") {
                    binding.imageMain.setImageResource(R.drawable.ic_rain)
                }
                if (i.icon == "11d" || i.icon == "11n") {
                    binding.imageMain.setImageResource(R.drawable.ic_storm)
                }
                if (i.icon == "13d" || i.icon == "13n") {
                    binding.imageMain.setImageResource(R.drawable.ic_snowflake)
                }
                if (i.icon == "50d" || i.icon == "50n") {
                    binding.imageMain.setImageResource(R.drawable.ic_tornado)
                }
            }
        })
        // Check for location permissions
        if (checkLocationPermissions()) {
            // Permissions are granted, proceed to get the current location
            getCurrentLocation()
        } else {
            // Request location permissions
            requestLocationPermissions()
        }
        val searchEditText = binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(Color.BLACK)
        binding.next5Days.setOnClickListener {
            startActivity(Intent(this, ForecastActivity::class.java))
        }
        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                val sharedPrefs = SharedPrefs.getInstance(this@MainActivity)
                sharedPrefs.setValueOrNull("city", query!!)
                if (!query.isNullOrEmpty()) {
                    viM.getWeather(query)
                    binding.searchView.setQuery("", false)
                    binding.searchView.clearFocus()
                    binding.searchView.isIconified = true
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
        binding.searchView.setOnSearchClickListener {
            binding.searchView.layoutParams.width = 970
            binding.searchView.requestLayout()
        }
        binding.searchView.setOnCloseListener {
            binding.searchView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.searchView.requestLayout()
            false
        }

        val cityNameTextView = binding.cityName
        val params = cityNameTextView.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(30.dpToPx(), params.topMargin, params.rightMargin, params.bottomMargin)
        cityNameTextView.layoutParams = params

    }
    private fun checkLocationPermissions(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }
    fun Int.dpToPx(): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (this * density).toInt()
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            Utils.PERMISSION_REQUEST_CODE
        )
    }
    // Handle the permission request result
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Utils.PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                // Permissions granted, get the current location
                getCurrentLocation()
            } else {
                // Permissions denied, handle accordingly
                // For example, show an error message or disable location-based features
            }
        }
    }
    // Function to get the current location
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val location: Location? =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                // Use the latitude and longitude values as needed
                // ...
                val myprefs = SharedPrefs(this)
                myprefs.setValue("lon", longitude.toString())
                myprefs.setValue("lat", latitude.toString())
                // Example: Display latitude and longitude in logs
                Toast.makeText(this, "Latitude: $latitude, Longitude: $longitude", Toast.LENGTH_SHORT).show()
                Log.d("Current Location", "Latitude: $latitude, Longitude: $longitude")
                // Reverse geocode the location to get address information
                reverseGeocodeLocation(latitude, longitude)
            } else {
                // Location is null, handle accordingly
                // For example, request location updates or show an error message
            }
        } else {
            // Location permission not granted, handle accordingly
            // For example, show an error message or disable location-based features
        }
    }
    // Function to reverse geocode the location and get address information
    private fun reverseGeocodeLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses!!.isNotEmpty()) {
            val address = addresses[0]
            val addressLine = address.getAddressLine(0)
            // Use the addressLine as needed
            // ...
            // Example: Display address in logs
            Log.d("Current Address", addressLine)
        } else {
            // No address found, handle accordingly
            // For example, show an error message or use default address values
        }
    }
}