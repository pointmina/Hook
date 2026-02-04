package com.hanto.hook.ui.view.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hanto.hook.R
import com.hanto.hook.data.TagSelectionListener
import com.hanto.hook.data.model.Hook
import com.hanto.hook.data.model.Tag
import com.hanto.hook.databinding.ActivityHookDetailBinding
import com.hanto.hook.ui.view.fragment.TagListFragment
import com.hanto.hook.viewmodel.HookViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HookDetailActivity : BaseActivity(), TagSelectionListener {

    companion object {
        private const val TAG = "HookDetailActivity"
        const val EXTRA_HOOK = "HOOK"
    }

    private lateinit var binding: ActivityHookDetailBinding

    // Hilt를 통해 ViewModel 자동 주입
    private val hookViewModel: HookViewModel by viewModels()

    private var isUrlValid = true
    private var isTitleValid = true

    private val multiChoiceList = linkedMapOf<String, Boolean>()
    private var tagsForHook: List<Tag>? = null
    private var tagNames: Set<String> = emptySet()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")
        super.onCreate(savedInstanceState)

        binding = ActivityHookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val hook: Hook? = intent.getParcelableExtra(EXTRA_HOOK)

        hook?.let {
            bindHookDetails(it)
            observeTagsForHook(it.hookId)
        }

        setupViews(hook)
        setupObservers()
        updateButtonState()
    }

    private fun setupViews(hook: Hook?) {
        binding.btnHookEdit.setOnClickListener {
            val updatedTitle = binding.tvHandedTitle.text.toString().trim()
            val updatedDescription = binding.tvHandedDesc.text.toString().trim()
            val updatedUrl = binding.tvHandedUrl.text.toString().trim()

            when {
                updatedTitle.isBlank() -> {
                    Toast.makeText(this, getString(R.string.plz_input_title), Toast.LENGTH_SHORT)
                        .show()
                }

                updatedUrl.isBlank() -> {
                    Toast.makeText(this, getString(R.string.plz_input_url), Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {
                    hook?.let { currentHook ->
                        val updatedHook = currentHook.copy(
                            title = updatedTitle,
                            url = updatedUrl,
                            description = updatedDescription,
                        )
                        hookViewModel.updateHookAndTags(updatedHook, tagNames.toList())
                        finish()
                    }
                }
            }
        }

        binding.tvTag.setOnClickListener {
            showTagListFragment()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // 1. 에러 메시지 관찰
                launch {
                    hookViewModel.errorMessage.collect { errorMessage ->
                        errorMessage?.let {
                            Toast.makeText(this@HookDetailActivity, it, Toast.LENGTH_SHORT).show()
                            hookViewModel.clearErrorMessage()
                        }
                    }
                }

                // 2. 로딩 상태 관찰 (버튼 활성화 여부 제어)
                launch {
                    hookViewModel.isLoading.collect { isLoading ->
                        binding.btnHookEdit.isEnabled = !isLoading && isUrlValid && isTitleValid
                    }
                }
            }
        }
    }

    private fun bindHookDetails(hook: Hook) {
        binding.apply {
            tvHandedTitle.setText(hook.title)
            tvHandedUrl.setText(hook.url)
            tvHandedDesc.setText(hook.description)
        }
    }

    private fun observeTagsForHook(hookId: String) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                hookViewModel.getTagsForHook(hookId).collect { fetchedTags ->
                    tagsForHook = fetchedTags
                    binding.tvTag.text = fetchedTags.toTagString()

                    tagNames = fetchedTags.map { it.name }.toSet()

                    fetchedTags.forEach { tag ->
                        multiChoiceList[tag.name] = true
                    }
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

    override fun onResume() {
        Log.d(TAG, "onResume()")
        super.onResume()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")
        super.onDestroy()
    }
}