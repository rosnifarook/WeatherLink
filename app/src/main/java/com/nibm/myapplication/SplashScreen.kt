package com.nibm.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.nibm.weatherbugapp.WeatherActivity

class SplashScreen : AppCompatActivity() {

    lateinit var topAnim : Animation
    lateinit var  bottomAnim : Animation
    lateinit var imgLogo : ImageView
    lateinit var lblCompanyName : TextView
    lateinit var lblMoto : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        imgLogo = findViewById(R.id.imgLogo)
        lblCompanyName = findViewById(R.id.lblCompanyName)
        lblMoto = findViewById(R.id.lblMoto)

        imgLogo.animation = topAnim
        lblCompanyName.animation = bottomAnim
        lblMoto.animation = bottomAnim

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, WeatherActivity::class.java)
            startActivity(intent)
            finish()
        }, 5000) // 5 seconds delay
    }
}