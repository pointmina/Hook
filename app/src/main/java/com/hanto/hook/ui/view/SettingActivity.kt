package com.hanto.hook.ui.view

import android.os.Bundle
import com.hanto.hook.BaseActivity
import com.hanto.hook.databinding.ActivitySettingBinding

class SettingActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivAppbarBackButton.setOnClickListener{
            finish()
        }



        binding.btnSaveChange.setOnClickListener{
            val newNickname = binding.tvUserName.text.toString()

        }

    }

}
