package com.hanto.hook.ui.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hanto.hook.BaseActivity
import com.hanto.hook.R
import com.hanto.hook.data.model.Hook
import com.hanto.hook.ui.adapter.SelectedTagHookListAdapter
import com.hanto.hook.databinding.ActivitySelectedTagBinding

class SelectedTagActivity : BaseActivity() {

    private lateinit var binding: ActivitySelectedTagBinding
    private lateinit var selectedTagHookListAdapter: SelectedTagHookListAdapter

    private var selectedTagId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectedTagBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivAppbarSelectedTagBackButton.setOnClickListener {
            finish()
        }

        // Intent 로부터 데이터 받기
        val selectedTagName = intent.getStringExtra("selectedTagName")
        selectedTagId = intent.getIntExtra("selectedTagId", -1) // 아이디 (기본값 -1으로 설정)
        binding.tvSelectedTag.text = selectedTagName

        val ivTagChange = binding.ivTagChange


        val ivTagDelete = binding.ivTagDelete
        ivTagDelete.setOnClickListener {
        }

        selectedTagHookListAdapter = SelectedTagHookListAdapter(
            hooks = ArrayList(),
            object : SelectedTagHookListAdapter.OnItemClickListener {
                override fun onClick(position: Int) {
                    val selectedHook = selectedTagHookListAdapter.getItem(position)
                    Intent(this@SelectedTagActivity, WebViewActivity::class.java).also { intent ->
                        intent.putExtra(WebViewActivity.EXTRA_URL, selectedHook.url)
                        startActivity(intent)
                    }
                }

                override fun onOptionButtonClick(position: Int) {
                    val selectedHook = selectedTagHookListAdapter.getItem(position)
                    showBottomSheetDialog(selectedHook)
                }
            })

        binding.rvUrlHookList.adapter = selectedTagHookListAdapter
        binding.rvUrlHookList.layoutManager = LinearLayoutManager(this)

        // DividerItemDecoration에 대한 설정
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        ResourcesCompat.getDrawable(resources, R.drawable.divider, null)?.let {
            dividerItemDecoration.setDrawable(it)
        }

        binding.rvUrlHookList.addItemDecoration(dividerItemDecoration)

    }



    @SuppressLint("InflateParams")
    private fun showBottomSheetDialog(selectedItem: Hook) {
        val dialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottom_dialog_home, null)
        dialog.setContentView(view)
        dialog.setCancelable(true)


    }


    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}
