package com.hanto.hook.ui.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hanto.hook.R

@Suppress("splash_screen")
@SuppressLint("CustomSplashScreen")
class SplashView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedPref = getSharedPreferences("hook_prefs", MODE_PRIVATE)
        val isFirstLaunch = sharedPref.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            // 첫 실행일 경우 OnboardingActivity로 이동
            startActivity(Intent(this, OnboardingActivity::class.java))
        } else {
            // 첫 실행이 아닐 경우 HomeActivity로 이동
            startActivity(Intent(this, HomeActivity::class.java))
        }
        finish()
    }

}
