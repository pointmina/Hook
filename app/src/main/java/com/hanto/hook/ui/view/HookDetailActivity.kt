package com.hanto.hook.ui.view

import HookRepository
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.hanto.hook.BaseActivity
import com.hanto.hook.R
import com.hanto.hook.data.TagSelectionListener
import com.hanto.hook.data.model.Hook
import com.hanto.hook.data.model.Tag
import com.hanto.hook.database.AppDatabase
import com.hanto.hook.database.DatabaseModule
import com.hanto.hook.databinding.ActivityHookDetailBinding
import com.hanto.hook.viewmodel.HookViewModel
import kotlinx.coroutines.launch

class HookDetailActivity : BaseActivity(), TagSelectionListener {

    val TAG = "HookDetailActivity"

    companion object {
        const val EXTRA_HOOK = "HOOK"
    }

    private lateinit var binding: ActivityHookDetailBinding

    private lateinit var hookRepository: HookRepository
    private lateinit var appDatabase: AppDatabase
    private val hookViewModel: HookViewModel by viewModels()

    private var isUrlValid = true
    private var isTitleValid = true

    private val multiChoiceList = linkedMapOf<String, Boolean>()
    private var tagsForHook: List<Tag>? = null
    private var tagNames: Set<String> = emptySet()  // Set으로 초기화

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")

        super.onCreate(savedInstanceState)
        binding = ActivityHookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 데이터베이스 초기화
        appDatabase = DatabaseModule.getDatabase()
        hookRepository = HookRepository(appDatabase)

        val hook: Hook? = intent.getParcelableExtra(EXTRA_HOOK)

        hook?.let {
            bindHookDetails(it)
            observeTagsForHook(it.hookId)
        }

        binding.btnHookEdit.setOnClickListener {
            if (tagNames.isNotEmpty()) {
                hook?.let {
                    lifecycleScope.launch {
                        hookViewModel.updateHookAndTags(it, tagNames.toList())
                        finish()
                    }
                }
            }
        }

        binding.tvTag.setOnClickListener {
            showTagListFragment()
        }

        updateButtonState()
    }

    private fun bindHookDetails(hook: Hook) {
        binding.apply {
            tvHandedTitle.setText(hook.title)
            tvHandedUrl.setText(hook.url)
            tvHandedDesc.setText(hook.description)
        }
    }

    private fun observeTagsForHook(hookId: String) {
        hookId.let { id ->
            hookViewModel.getTagsForHook(id)?.observe(this) { fetchedTags ->
                tagsForHook = fetchedTags
                binding.tvTag.text = fetchedTags.toTagString()

                // Set으로 중복 없이 tagNames 관리
                tagNames = fetchedTags.map { it.name }.toSet()

                fetchedTags.forEach { tag ->
                    multiChoiceList[tag.name] = true
                }
            }
        }
    }

    private fun List<Tag>.toTagString(): String {
        return joinToString(separator = " ") { tag -> "#${tag.name}" }
    }

    private fun updateButtonState() {
        val isValid = isUrlValid && isTitleValid
        binding.btnHookEdit.apply {
            isEnabled = isValid
            val color = if (isValid) R.color.purple else R.color.gray_100
            setBackgroundColor(ContextCompat.getColor(this@HookDetailActivity, color))
        }
    }

    override fun onResume() {
        Log.d(TAG, "onResume()")
        super.onResume()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")
        super.onDestroy()
    }

    private fun updateTagsInView() {
        val selectedTags = multiChoiceList.filterValues { it }.keys
        tagNames = selectedTags.toSet()  // Set으로 tagNames 업데이트
        binding.tvTag.text = selectedTags.toSortedSet().joinToString("  ") { "#$it" }
    }

    override fun onTagsSelected(tags: List<String>) {
        tags.forEach { tag ->
            if (!multiChoiceList.containsKey(tag)) {
                multiChoiceList[tag] = true
            }
        }
        updateTagsInView()
    }

    private fun showTagListFragment() {
        val fragment = TagListFragment.newInstance(multiChoiceList)
        fragment.setTagSelectionListener(this)
        fragment.show(supportFragmentManager, "TagListFragment")
    }
}
