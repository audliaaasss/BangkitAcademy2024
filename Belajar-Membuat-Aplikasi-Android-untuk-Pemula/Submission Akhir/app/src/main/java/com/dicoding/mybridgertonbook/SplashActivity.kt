package com.dicoding.mybridgertonbook

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.ImageView

class SplashActivity : AppCompatActivity() {
    private lateinit var splashLogo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        splashLogo = findViewById(R.id.splash_logo)

        val layoutParams = splashLogo.layoutParams
        layoutParams.width = (resources.displayMetrics.widthPixels * 0.6).toInt()
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        splashLogo.layoutParams = layoutParams

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}