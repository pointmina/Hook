package com.hanto.hook.ui.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanto.hook.R
import com.hanto.hook.databinding.ActivitySelectedTagBinding
import com.hanto.hook.ui.adapter.SelectedTagHookListAdapter
import com.hanto.hook.ui.view.fragment.CommonDialogFragment // [변경] 통합 다이얼로그 Import
import com.hanto.hook.util.BottomDialogHelper
import com.hanto.hook.viewmodel.HookViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectedTagActivity : BaseActivity() {

    companion object {
        private const val TAG = "SelectedTagActivity"
    }

    private lateinit var binding: ActivitySelectedTagBinding
    private lateinit var selectedTagHookListAdapter: SelectedTagHookListAdapter

    private val hookViewModel: HookViewModel by viewModels()

    private var selectedTagName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")

        binding = ActivitySelectedTagBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupRecyclerView()
        setupObservers()
        getIntentData()
    }

    private fun setupViews() {
        binding.ivAppbarSelectedTagBackButton.setOnClickListener {
            finish()
        }

        binding.ivTagChange.setOnClickListener {
            showChangeTagFragment()
        }

        binding.ivTagDelete.setOnClickListener {
            showDeleteTagFragment()
        }
    }

    private fun setupRecyclerView() {
        selectedTagHookListAdapter = SelectedTagHookListAdapter(
            hooks = ArrayList(),
            object : SelectedTagHookListAdapter.OnItemClickListener {
                override fun onClick(position: Int) {
                    val selectedHook = selectedTagHookListAdapter.getItem(position)
                    selectedHook?.let { hook ->
                        val intent =
                            Intent(this@SelectedTagActivity, WebViewActivity::class.java).apply {
                                putExtra("HOOK_URL", hook.url)
                            }
                        startActivity(intent)
                    }
                }

                override fun onOptionButtonClick(position: Int) {
                    val selectedHook = selectedTagHookListAdapter.getItem(position)
                    selectedHook?.let { hook ->
                        BottomDialogHelper.showHookOptionsDialog(
                            this@SelectedTagActivity, hook, hookViewModel
                        )
                    }
                }
            }
        )

        binding.rvUrlHookList.apply {
            adapter = selectedTagHookListAdapter
            layoutManager = LinearLayoutManager(this@SelectedTagActivity)
            addItemDecoration(
                DividerItemDecoration(
                    this@SelectedTagActivity,
                    DividerItemDecoration.VERTICAL
                ).apply {
                    ResourcesCompat.getDrawable(resources, R.drawable.divider, null)?.let {
                        setDrawable(it)
                    }
                }
            )
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 1. 선택된 태그의 훅 리스트 관찰
                launch {
                    hookViewModel.hooksBySelectedTag.collect { hooks ->
                        if (hooks.isNotEmpty()) {
                            val distinctHooks = hooks.distinctBy { it.hook.hookId }
                            Log.d(TAG, "Distinct Hooks fetched: ${distinctHooks.size}")
                            selectedTagHookListAdapter.submitList(distinctHooks)
                            binding.tvTagCount.text = distinctHooks.size.toString()
                        } else {
                            selectedTagHookListAdapter.submitList(emptyList())
                            binding.tvTagCount.text = "0"
                        }
                    }
                }

                // 2. 에러 메시지 관찰
                launch {
                    hookViewModel.errorMessage.collect { errorMessage ->
                        errorMessage?.let {
                            Log.e(TAG, "Error: $it")
                            Toast.makeText(this@SelectedTagActivity, it, Toast.LENGTH_SHORT).show()
                            hookViewModel.clearErrorMessage()
                        }
                    }
                }

                // 태그 삭제
                launch {
                    hookViewModel.deleteSuccessEvent.collect {
                        val intent = Intent().apply {
                            putExtra("EXTRA_TAG_NAME", selectedTagName) // 삭제된 태그 이름
                            putExtra("ACTION_TYPE", "DELETE")           // 작업 타입
                        }
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun getIntentData() {
        selectedTagName = intent.getStringExtra("selectedTagName").orEmpty()
        binding.tvSelectedTag.text = selectedTagName

        if (selectedTagName == HookViewModel.TAG_UNCATEGORIZED) {
            binding.ivTagChange.visibility = View.GONE
            binding.ivTagDelete.visibility = View.GONE
        } else {
            binding.ivTagChange.visibility = View.VISIBLE
            binding.ivTagDelete.visibility = View.VISIBLE
        }

        hookViewModel.selectTagName(selectedTagName)
    }

    private fun showChangeTagFragment() {
        val dialog = CommonDialogFragment.newInputInstance(
            title = getString(R.string.question_really_modi_tag),
            hint = getString(R.string.plz_input_tag),
            prefilledText = selectedTagName,
            positiveText = getString(R.string.description_save_changes),
            onInputConfirm = { newTagName ->
                hookViewModel.updateTagName(selectedTagName, newTagName)

                selectedTagName = newTagName
                binding.tvSelectedTag.text = newTagName
                hookViewModel.selectTagName(newTagName)
            }
        )
        dialog.show(supportFragmentManager, CommonDialogFragment.TAG)
    }

    private fun showDeleteTagFragment() {
        val dialog = CommonDialogFragment.newInstance(
            title = getString(R.string.question_really_delete_tag),
            message = getString(R.string.delete_tag_content),
            positiveText = getString(R.string.delete_tag),
            onPositiveClick = {
                hookViewModel.deleteTagByTagName(selectedTagName)
            }
        )
        dialog.show(supportFragmentManager, CommonDialogFragment.TAG)
    }

    override fun onDestroy() {
        super.onDestroy()
        hookViewModel.clearSelectedTag()
        Log.d(TAG, "onDestroy()")
    }
}