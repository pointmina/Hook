package com.hanto.hook.ui.view.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hanto.hook.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("splash_screen")
@SuppressLint("CustomSplashScreen")
class SplashView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            delay(1000)
            navigateToNextScreen()
        }
    }

    private fun navigateToNextScreen() {
        val sharedPref = getSharedPreferences("hook_prefs", MODE_PRIVATE)
        val isFirstLaunch = sharedPref.getBoolean("isFirstLaunch", true)
//        val isFirstLaunch = true

        val intent = if (isFirstLaunch) {
            Intent(this, TutorialActivity::class.java)
        } else {
            Intent(this, HomeActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}


