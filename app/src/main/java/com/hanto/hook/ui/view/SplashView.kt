package com.hanto.hook.ui.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.hanto.hook.R

@Suppress("splash_screen")
@SuppressLint("CustomSplashScreen")
class SplashView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        splashToMain()
    }
    private fun splashToMain() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashView, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 500)
    }
}
