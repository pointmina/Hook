package com.hanto.hook.ui.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hanto.hook.BaseActivity
import com.hanto.hook.R
import com.hanto.hook.data.model.Hook
import com.hanto.hook.databinding.ActivitySelectedTagBinding
import com.hanto.hook.ui.adapter.SelectedTagHookListAdapter
import com.hanto.hook.viewmodel.HookViewModel

class SelectedTagActivity : BaseActivity() {

    val TAG = "SelectedTagActivity"

    private lateinit var binding: ActivitySelectedTagBinding
    private lateinit var selectedTagHookListAdapter: SelectedTagHookListAdapter

    private lateinit var hookViewModel: HookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"onCreate()")
        super.onCreate(savedInstanceState)

        binding = ActivitySelectedTagBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivAppbarSelectedTagBackButton.setOnClickListener {
            finish()
        }

        val selectedTagName = intent.getStringExtra("selectedTagName")
        binding.tvSelectedTag.text = selectedTagName

        hookViewModel = ViewModelProvider(this)[HookViewModel::class.java]

        selectedTagName?.let { tagName ->
            hookViewModel.getHooksByTagName(tagName).observe(this) { hooks ->
                if (hooks != null) {
                    selectedTagHookListAdapter.submitList(hooks)
                    binding.tvTagCount.text = hooks.size.toString()
                }
            }
        }

        binding.ivTagChange.setOnClickListener {
        }

        binding.ivTagDelete.setOnClickListener {
        }

        // 어댑터 설정
        selectedTagHookListAdapter = SelectedTagHookListAdapter(
            hooks = ArrayList(),
            object : SelectedTagHookListAdapter.OnItemClickListener {
                override fun onClick(position: Int) {
                    val selectedHook = selectedTagHookListAdapter.getItem(position)
                    Intent(this@SelectedTagActivity, WebViewActivity::class.java).also { intent ->
                        if (selectedHook != null) {
                            intent.putExtra(WebViewActivity.EXTRA_URL, selectedHook.url)
                        }
                        startActivity(intent)
                    }
                }

                override fun onOptionButtonClick(position: Int) {
                    val selectedHook = selectedTagHookListAdapter.getItem(position)
                    if (selectedHook != null) {
                        showBottomSheetDialog(selectedHook)
                    }
                }
            })

        binding.rvUrlHookList.adapter = selectedTagHookListAdapter
        binding.rvUrlHookList.layoutManager = LinearLayoutManager(this)

        // DividerItemDecoration 설정
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
        Log.d(TAG,"onResume()")
        super.onResume()
    }

    override fun onDestroy() {
        Log.d(TAG,"onDestroy()")
        super.onDestroy()
    }


}
