package com.hanto.hook.ui.view

import HookRepository
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
            val updatedTitle = binding.tvHandedTitle.text.toString().trim()
            val updatedDescription = binding.tvHandedDesc.text.toString().trim()
            val updatedUrl = binding.tvHandedUrl.text.toString().trim()

            when {
                updatedTitle.isBlank() -> {
                    Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }

                updatedUrl.isBlank() -> {
                    Toast.makeText(this, "URL을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    hook?.let { currentHook ->
                        val updatedHook = currentHook.copy(
                            title = updatedTitle,
                            url = updatedUrl,
                            description = updatedDescription,
                        )

                        lifecycleScope.launch {
                            runCatching {
                                hookViewModel.updateHookAndTags(updatedHook, tagNames.toList())
                            }.onSuccess {
                                finish()
                            }.onFailure { e ->
                                Log.e(TAG, "Error updating hook", e)
                                Toast.makeText(
                                    this@HookDetailActivity,
                                    "업데이트 실패. 다시 시도해주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
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

                tagNames = fetchedTags.map { it.name }.toSet()

                fetchedTags.forEach { tag ->
                    multiChoiceList[tag.name] = true
                }
            }
        }
    }

    private fun List<Tag>.toTagString(): String {
        return distinctBy { it.name }
            .joinToString(separator = " ") { tag -> "#${tag.name}" }
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
        tagNames = selectedTags.toSet()
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
