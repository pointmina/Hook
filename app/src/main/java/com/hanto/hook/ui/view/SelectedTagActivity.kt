package com.hanto.hook.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanto.hook.BaseActivity
import com.hanto.hook.R
import com.hanto.hook.data.TagUpdateListener
import com.hanto.hook.databinding.ActivitySelectedTagBinding
import com.hanto.hook.ui.adapter.SelectedTagHookListAdapter
import com.hanto.hook.util.BottomDialogHelper
import com.hanto.hook.viewmodel.HookViewModel

class SelectedTagActivity : BaseActivity(), TagUpdateListener {

    private val TAG = "SelectedTagActivity"
    private lateinit var binding: ActivitySelectedTagBinding
    private lateinit var selectedTagHookListAdapter: SelectedTagHookListAdapter

    private val hookViewModel: HookViewModel by viewModels()

    private var selectedTagName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")

        // Binding 초기화
        binding = ActivitySelectedTagBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로 가기 버튼 처리
        binding.ivAppbarSelectedTagBackButton.setOnClickListener {
            finish()
        }

        // 태그 이름을 인텐트로 받아옴
        selectedTagName = intent.getStringExtra("selectedTagName").orEmpty()
        binding.tvSelectedTag.text = selectedTagName

        // 어댑터 설정
        setupRecyclerView()

        // 태그 훅 데이터 관찰
        observeTagHooks(selectedTagName)

        // 태그 변경 버튼 클릭
        binding.ivTagChange.setOnClickListener {
            val changeTagFragment = ChangeTagFragment()
            val bundle = Bundle().apply {
                putString("selectedTag", selectedTagName)
            }
            changeTagFragment.arguments = bundle
            changeTagFragment.setTagUpdateListener(this)
            changeTagFragment.show(supportFragmentManager, "ChangeTagFragment")
        }

        // 태그 삭제 버큰 클릭
        binding.ivTagDelete.setOnClickListener {
            val deleteTagFragment = DeleteTagFragment()
            deleteTagFragment.setOnTagDeletedListener(object :
                DeleteTagFragment.OnTagDeletedListener {
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


    }

    private fun setupRecyclerView() {
        selectedTagHookListAdapter = SelectedTagHookListAdapter(hooks = ArrayList(),
            object : SelectedTagHookListAdapter.OnItemClickListener {
                override fun onClick(position: Int) {
                    val selectedHook = selectedTagHookListAdapter.getItem(position)
                    Intent(this@SelectedTagActivity, WebViewActivity::class.java).also { intent ->
                        selectedHook?.let { hook ->
                            intent.putExtra(WebViewActivity.EXTRA_URL, hook.url)
                        }
                        startActivity(intent)
                    }
                }

                override fun onOptionButtonClick(position: Int) {
                    val selectedHook = selectedTagHookListAdapter.getItem(position)
                    selectedHook?.let { hook ->
                        BottomDialogHelper.showHookOptionsDialog(
                            this@SelectedTagActivity, hook, hookViewModel,
                        )
                    }
                }
            })

        binding.rvUrlHookList.apply {
            adapter = selectedTagHookListAdapter
            layoutManager = LinearLayoutManager(this@SelectedTagActivity)
            addItemDecoration(DividerItemDecoration(
                this@SelectedTagActivity, DividerItemDecoration.VERTICAL
            ).apply {
                ResourcesCompat.getDrawable(resources, R.drawable.divider, null)?.let {
                    setDrawable(it)
                }
            })
        }
    }

    private fun observeTagHooks(tagName: String) {
        Log.d(TAG, "Observing hooks for tag: $tagName")

        val liveData = hookViewModel.getHooksByTagName(tagName)
        liveData.observe(this) { hooks ->
            if (hooks != null && hooks.isNotEmpty()) {

                // 중복 제거
                val distinctHooks = hooks.distinctBy { it.id }

                Log.d(TAG, "Distinct Hooks fetched: ${distinctHooks.size}")
                selectedTagHookListAdapter.submitList(distinctHooks)
                binding.tvTagCount.text = distinctHooks.size.toString()

                // 0보다 큰 값이 확인되면 관찰 중단
//                liveData.removeObservers(this)

            } else {
                Log.d(TAG, "Hooks fetched: 0")
                selectedTagHookListAdapter.submitList(emptyList())
            }
        }
    }


    override fun onTagUpdated(newTagName: String) {
        Log.d(TAG, "onTagUpdated: $newTagName")
        selectedTagName = newTagName
        binding.tvSelectedTag.text = newTagName

        observeTagHooks(newTagName)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
    }
}
