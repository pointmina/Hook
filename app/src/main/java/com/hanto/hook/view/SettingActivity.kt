package com.hanto.hook.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hanto.hook.BaseActivity
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.databinding.ActivitySettingBinding
import com.hanto.hook.viewmodel.MainViewModel
import com.hanto.hook.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class SettingActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingBinding
    private val apiServiceManager by lazy { ApiServiceManager() }
    private val viewModelFactory by lazy { ViewModelFactory(apiServiceManager) }
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivAppbarBackButton.setOnClickListener{
            finish()
        }

        viewModel.loadGetMyInfo()
        viewModel.userData.observe(this) { user ->
            val nickname = user?.user?.nickname ?: "종합설계"
            binding.tvUserName.setText(nickname)
        }

        binding.btnSaveChange.setOnClickListener{
            val newNickname = binding.tvUserName.text.toString()
            viewModel.loadUpdateNickName(nickname = newNickname)
            viewModel.successData.observe(this)  { successData ->
                Toast.makeText(this, "${successData?.result?.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.kakaoLogoutButton.setOnClickListener {
            lifecycleScope.launch {
                clearTokens()
                val intent = Intent(this@SettingActivity, SplashView::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
    private suspend fun clearTokens() {
        val accessTokenKey = stringPreferencesKey("access_token")
        val refreshTokenKey = stringPreferencesKey("refresh_token")
        applicationContext.dataStore.edit { preferences ->
            preferences.remove(accessTokenKey)
            preferences.remove(refreshTokenKey)
        }
    }
}
