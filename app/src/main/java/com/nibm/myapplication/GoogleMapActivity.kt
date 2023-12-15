package com.nibm.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.nibm.weatherbugapp.WeatherActivity

class GoogleMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var myMap: GoogleMap
    private lateinit var btnBack : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        btnBack = findViewById(R.id.btnBack)

        // Set an OnClickListener for the button
        btnBack.setOnClickListener {
            // Create an Intent to navigate back to the WeatherActivity
            val intent = Intent(this@GoogleMapActivity, WeatherActivity::class.java)

            // Start the ForeCastDashBoard activity
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
        val sydney = LatLng(6.92, 79.86)
        myMap.addMarker(MarkerOptions().position(sydney).title("Colombo"))
        myMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}
