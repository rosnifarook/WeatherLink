package com.nibm.weatherbugapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nibm.myapplication.AboutUsActivity
import com.nibm.myapplication.GoogleMapActivity
import com.nibm.myapplication.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WeatherActivity : AppCompatActivity() {

    private lateinit var txtDataAndTime: TextView
    private lateinit var txtCountry: TextView
    private lateinit var txtCelcius2: TextView
    private lateinit var imgWeatherImg: ImageView
    private lateinit var txtPressureDetails: TextView
    private lateinit var txtHumidityDetails: TextView
    private lateinit var txtTempDetails: TextView
    private lateinit var txtWeatherDetails: TextView
    private lateinit var lbl_description :TextView
    private lateinit var temp_max : TextView
    private lateinit var temp_min : TextView
    private lateinit var sea_level : TextView
    private lateinit var grnd_level : TextView
    private lateinit var btnAboutUs : Button
    private lateinit var btnMap : ImageButton
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val apiKey = "7df53ab1cb496bd0eed2ef64eddec83e"

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_home)
        val button: Button = findViewById(R.id.futurecard)

        // Set a click listener for the button
        button.setOnClickListener(View.OnClickListener {
            // Create an Intent to navigate to ForeCastDashBoard
            val intent = Intent(this@WeatherActivity, ForcastActivity::class.java)

            // Start the ForeCastDashBoard activity
            startActivity(intent)
        })
        txtDataAndTime = findViewById(R.id.txt_dataAndTime)
        txtCountry = findViewById(R.id.txt_country)
        txtCelcius2 = findViewById(R.id.txt_celcius2)
//        txtDescription = findViewById(R.id.txt_description)
        imgWeatherImg = findViewById(R.id.img_weatherImg)
        lbl_description = findViewById(R.id.txt_des)
        temp_max = findViewById(R.id.temp_max)
        temp_min = findViewById(R.id.temp_min)
        sea_level = findViewById(R.id.sea_level)
        grnd_level = findViewById(R.id.grnd_level)
        btnAboutUs = findViewById(R.id.btnAboutUs)
        btnMap = findViewById(R.id.btnMap)

        // Initialize the additional TextView elements
        txtPressureDetails = findViewById(R.id.txt_pressureDetails)
        txtHumidityDetails = findViewById(R.id.txt_humidityDetails)
        txtTempDetails = findViewById(R.id.txt_TempDetails)
        txtWeatherDetails = findViewById(R.id.txt_WindSpeedDetails) // Updated ID

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
        // Get the current date and time
        getCurrentDateTime()
        requestPermission()
        // Get the current location, temperature, weather description, and weather icon for the current location
        CoroutineScope(Dispatchers.Main).launch {
            delay(4000)
            // Get the current location, temperature, weather description, and weather icon for the current location
            getCurrentLocation()
        }

        btnMap.setOnClickListener {
            // Create an Intent to navigate to ForeCastDashBoard
            val intent = Intent(this@WeatherActivity, GoogleMapActivity::class.java)

            // Start the ForeCastDashBoard activity
            startActivity(intent)
        }



        // Set an OnClickListener for the button
        btnAboutUs.setOnClickListener {
            // Create an Intent to navigate to ForeCastDashBoard
            val intent = Intent(this@WeatherActivity, AboutUsActivity::class.java)

            // Start the ForeCastDashBoard activity
            startActivity(intent)
        }

        // Add the new code for handling the drawableRight click
        val searchBar = findViewById<EditText>(R.id.search_bar)
        searchBar.setOnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (searchBar.right - searchBar.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    // The drawableRight icon was clicked
                    onSearchButtonClick()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun onSearchButtonClick() {
        // Place the code you want to execute when the search button or drawableRight is clicked
        val cityName = findViewById<EditText>(R.id.search_bar).text.toString()
        if (cityName.isNotEmpty()) {
            // Call a method to get weather information for the searched city
            getWeatherForCity(cityName)
        } else {
            Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentDateTime() {
        // Get the current date and time using the device's time zone
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("MMMM dd (EEE) | hh:mm a", Locale.getDefault())
        val formattedDate = sdf.format(calendar.time)

        // Display the formatted date and time
        txtDataAndTime.text = formattedDate
    }

    private fun requestPermission()
    {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }
    private fun getCurrentLocation() {

        // Check location permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Use fusedLocationClient to get the current location
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // Assuming you are inside an Activity
// Get and display the current temperature, weather description, and weather icon

                        val forecastCityName = getCityName(location.latitude, location.longitude)
                        txtCountry .text = forecastCityName
                        getWeatherData(location.latitude, location.longitude)

                    } else {
                        // Assuming you are inside an Activity

                        // Handle the case when location is null
                        txtCountry.text = "Location: Unknown"
                    }
                }
                .addOnFailureListener { e ->
                    // Handle errors that may occur while getting the location
                    Toast.makeText(
                        this,
                        "Error getting location: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun getWeatherData(latitude: Double, longitude: Double) {
        val apiUrl =
            "https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=$apiKey"
        val request = JsonObjectRequest(
            Request.Method.GET, apiUrl, null,
            { data ->
                try {
                    val cityName = data.getString("name")
                    val temperature = data.getJSONObject("main").getDouble("temp")
                    val temperatureInCelsius = temperature - 273.15
                    val formattedTemperature = String.format("%.2f", temperatureInCelsius)
                    val pressure = data.getJSONObject("main").getDouble("pressure")
                    val humidity = data.getJSONObject("main").getDouble("humidity")
                    val windSpeed = data.getJSONObject("wind").getDouble("speed")
                    val weatherArray = data.getJSONArray("weather")
                    val description = if (weatherArray.length() > 0) {
                        weatherArray.getJSONObject(0).getString("description")
                    } else {
                        ""
                    }
                    lbl_description
                    val iconCode = if (weatherArray.length() > 0) {
                        weatherArray.getJSONObject(0).getString("icon")
                    } else {
                        ""
                    }

                    // Display the location name, temperature, and weather description
                    //  txtCountry.text = cityName
                    txtCelcius2.text = "${formattedTemperature}°C"
//                    txtDescription.text = description.toUpperCase()

                    // Display the additional weather details
                    txtPressureDetails.text = "$pressure hPa"
                    txtHumidityDetails.text = "$humidity%"
                    txtTempDetails.text = "$temperature°C"
                    txtWeatherDetails.text = "${windSpeed} m/s"

                    // Display the weather icon
                    displayWeatherIcon(iconCode)
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "Error parsing weather information",
                        Toast.LENGTH_SHORT
                    ).show()
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(
                    this,
                    "Error loading weather information",
                    Toast.LENGTH_SHORT
                ).show()
                error.printStackTrace()
            }
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun getWeatherForCity(cityName: String) {
        val apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=$apiKey"
        val request = JsonObjectRequest(
            Request.Method.GET, apiUrl, null,
            { data ->
                // Handle the JSON response for the searched city
                try {
                    // Extract weather information and update UI
                    val temperature = data.getJSONObject("main").getDouble("temp")
                    val max_temperature = data.getJSONObject("main").getDouble("temp_max")
                    val min_temperature = data.getJSONObject("main").getDouble("temp_min")

                    val maxTempInCelsius = max_temperature - 273.15
                    val formattedMaxTemperature = String.format("%.2f", maxTempInCelsius)

                    val minTempInCelsius = min_temperature - 273.15
                    val formattedMinTemperature = String.format("%.2f", minTempInCelsius)

                    val sea_level1 = data.getJSONObject("main").getDouble("sea_level")
                    val groud_level = data.getJSONObject("main").getDouble("grnd_level")


                    val temperatureInCelsius = temperature - 273.15
                    val formattedTemperature = String.format("%.2f", temperatureInCelsius)
                    val pressure = data.getJSONObject("main").getDouble("pressure")
                    val humidity = data.getJSONObject("main").getDouble("humidity")
                    val windSpeed = data.getJSONObject("wind").getDouble("speed")
                    val weatherArray = data.getJSONArray("weather")
                    val description = if (weatherArray.length() > 0) {
                        weatherArray.getJSONObject(0).getString("description")
                    } else {
                        ""
                    }

                    val iconCode = if (weatherArray.length() > 0) {
                        weatherArray.getJSONObject(0).getString("icon")
                    } else {
                        ""
                    }

                    // Display the location name, temperature, and weather description
                    txtCountry.text = cityName
                    txtCelcius2.text = "${formattedTemperature}°C"
//                    txtDescription.text = description.toUpperCase()
                    lbl_description.text = description.toUpperCase()

                    temp_max.text = "${formattedMaxTemperature}°C"
                    temp_min.text = "${formattedMinTemperature}°C"

                    sea_level.text = "$sea_level1"
                    grnd_level.text = "$groud_level"

                    // Display the additional weather details
                    txtPressureDetails.text = "$pressure hPa"
                    txtHumidityDetails.text = "$humidity%"
                    txtTempDetails.text = "$temperature°C"
                    txtWeatherDetails.text = "${windSpeed} m/s"

                    // Display the weather icon
                    displayWeatherIcon(iconCode)
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "Error parsing weather information",
                        Toast.LENGTH_SHORT
                    ).show()
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(
                    this,
                    "Error loading weather information",
                    Toast.LENGTH_SHORT
                ).show()
                error.printStackTrace()
            }
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun displayWeatherIcon(iconCode: String) {
        // Construct the URL for the weather icon
        val iconUrl = "https://openweathermap.org/img/w/$iconCode.png"

        // Use Picasso library to load and display the weather icon
        Picasso.get().load(iconUrl).into(imgWeatherImg)
    }

    private fun getCityName(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val cityName = addresses[0]?.locality
                    return cityName ?: "Unknown"
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "Unknown"
    }
}