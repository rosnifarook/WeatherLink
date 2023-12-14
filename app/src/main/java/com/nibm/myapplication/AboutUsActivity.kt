package com.nibm.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.nibm.weatherbugapp.WeatherActivity

@SuppressLint("WrongViewCast")
class AboutUsActivity : AppCompatActivity() {

    private lateinit var btnBack : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        btnBack = findViewById(R.id.btnBack)

        // Set an OnClickListener for the button
        btnBack.setOnClickListener {
            // Create an Intent to navigate back to the WeatherActivity
            val intent = Intent(this@AboutUsActivity, WeatherActivity::class.java)

            // Start the ForeCastDashBoard activity
            startActivity(intent)
        }
    }
}