package com.hanto.hook.ui.view.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanto.hook.R
import com.hanto.hook.data.TagUpdateListener
import com.hanto.hook.databinding.ActivitySelectedTagBinding
import com.hanto.hook.ui.adapter.SelectedTagHookListAdapter
import com.hanto.hook.ui.view.Fragment.ChangeTagFragment
import com.hanto.hook.ui.view.Fragment.DeleteTagFragment
import com.hanto.hook.util.BottomDialogHelper
import com.hanto.hook.viewmodel.HookViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectedTagActivity : BaseActivity(), TagUpdateListener {

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
        hookViewModel.hooksBySelectedTag.observe(this) { hooks ->
            if (hooks.isNotEmpty()) {
                val distinctHooks = hooks.distinctBy { it.id }
                Log.d(TAG, "Distinct Hooks fetched: ${distinctHooks.size}")
                selectedTagHookListAdapter.submitList(distinctHooks)
                binding.tvTagCount.text = distinctHooks.size.toString()
            } else {
                selectedTagHookListAdapter.submitList(emptyList())
                binding.tvTagCount.text = "0"
            }
        }

        hookViewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Log.e(TAG, "Error: $it")
                hookViewModel.clearErrorMessage()
            }
        }
    }

    private fun getIntentData() {
        selectedTagName = intent.getStringExtra("selectedTagName").orEmpty()
        binding.tvSelectedTag.text = selectedTagName

        hookViewModel.selectTagName(selectedTagName)
    }

    private fun showChangeTagFragment() {
        val changeTagFragment = ChangeTagFragment()
        val bundle = Bundle().apply {
            putString("selectedTag", selectedTagName)
        }
        changeTagFragment.arguments = bundle
        changeTagFragment.setTagUpdateListener(this)
        changeTagFragment.show(supportFragmentManager, "ChangeTagFragment")
    }

    private fun showDeleteTagFragment() {
        val deleteTagFragment = DeleteTagFragment()
        deleteTagFragment.setOnTagDeletedListener(object : DeleteTagFragment.OnTagDeletedListener {
            override fun onTagDeleted() {
                finish()
            }
        })
        val bundle = Bundle().apply {
            putString("selectedTag", selectedTagName)
        }
        deleteTagFragment.arguments = bundle
        deleteTagFragment.show(supportFragmentManager, "DeleteTagFragment")
    }

    override fun onTagUpdated(tag: String) {
        Log.d(TAG, "onTagUpdated: $tag")
        selectedTagName = tag
        binding.tvSelectedTag.text = tag

        hookViewModel.selectTagName(tag)
    }

    override fun onDestroy() {
        super.onDestroy()
        hookViewModel.clearSelectedTag()
        Log.d(TAG, "onDestroy()")
    }
}