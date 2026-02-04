package com.hanto.hook.ui.view.activity

import android.content.ClipboardManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hanto.hook.R
import com.hanto.hook.data.TagSelectionListener
import com.hanto.hook.data.model.Hook
import com.hanto.hook.databinding.ActivityAddHookBinding
import com.hanto.hook.ui.view.fragment.TagListFragment
import com.hanto.hook.util.DateUtils
import com.hanto.hook.util.UrlUtils
import com.hanto.hook.viewmodel.HookViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddHookActivity : BaseActivity(), TagSelectionListener {

    companion object {
        private const val TAG = "AddHookActivity"
        private const val MAX_TITLE_LENGTH = 120
        private const val MAX_DESCRIPTION_LENGTH = 80
    }

    private lateinit var binding: ActivityAddHookBinding

    // Hilt를 통해 ViewModel 자동 주입
    private val hookViewModel: HookViewModel by viewModels()

    private var isUrlValid = false
    private var isTitleValid = false
    private var isExpanded = false

    private var selectedTags: List<String> = emptyList()
    private val multiChoiceList = linkedMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")
        super.onCreate(savedInstanceState)

        binding = ActivityAddHookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInitialTags()
        setupViews()
        setupTextWatchers()
        setupObservers()
        updateButtonState()
    }

    private fun setupInitialTags() {
        val tags = intent.getStringArrayListExtra("item_tag_list")
        tags?.forEach { tag ->
            multiChoiceList[tag] = true
        }
    }

    private fun setupViews() {
        binding.ivAppbarBackButton.setOnClickListener {
            finish()
        }

        binding.ivUrlLink.setOnClickListener {
            pasteUrlFromClipboard()
        }

        binding.containerTag.setOnClickListener {
            val fragment = TagListFragment.newInstance(multiChoiceList)
            fragment.setTagSelectionListener(this)
            fragment.show(supportFragmentManager, "TagListFragment")
        }

        binding.ivAddNewHook.setOnClickListener {
            insertHookIntoDB()
        }

        updateLimitStrings()
    }

    private fun setupTextWatchers() {
        binding.tvUrlLink.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()
                isUrlValid = input.isNotBlank() && !input.contains(" ")

                binding.tvGuide.visibility = if (!isUrlValid) View.VISIBLE else View.GONE
                updateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.tvUrlLink.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.tvUrlTitle.requestFocus()
                true
            } else {
                false
            }
        }

        binding.tvUrlTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    val limitString1 = "${s.length} / $MAX_TITLE_LENGTH"
                    binding.tvLimit1.text = limitString1

                    isTitleValid = s.toString().trim().isNotEmpty()
                    binding.tvGuideTitle.visibility = if (!isTitleValid) View.VISIBLE else View.GONE
                    updateButtonState()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.tvUrlTitle.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT && isExpanded) {
                binding.tvUrlDescription.requestFocus()
                true
            } else if (actionId == EditorInfo.IME_ACTION_NEXT && !isExpanded) {
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.tvUrlTitle.windowToken, 0)
                true
            } else {
                false
            }
        }

        binding.tvUrlDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    val limitString2 = "${s.length} / $MAX_DESCRIPTION_LENGTH"
                    binding.tvLimit2.text = limitString2
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // 1. 에러 메시지 관찰
                launch {
                    hookViewModel.errorMessage.collect { errorMessage ->
                        errorMessage?.let {
                            Toast.makeText(this@AddHookActivity, it, Toast.LENGTH_SHORT).show()
                            hookViewModel.clearErrorMessage()
                        }
                    }
                }

                // 2. 로딩 상태 관찰 (버튼 활성화/비활성화 제어)
                launch {
                    hookViewModel.isLoading.collect { isLoading ->
                        binding.ivAddNewHook.isEnabled = !isLoading && isUrlValid && isTitleValid
                    }
                }
            }
        }
    }

    private fun pasteUrlFromClipboard() {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.hasPrimaryClip()) {
            val clipData = clipboard.primaryClip
            if (clipData != null && clipData.itemCount > 0) {
                val item = clipData.getItemAt(0)
                val pasteData = item.text?.toString()

                if (pasteData != null && UrlUtils.isValidUrl(pasteData)) {
                    binding.tvUrlLink.setText(pasteData)
                    Toast.makeText(this, getString(R.string.get_current_url), Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, getString(R.string.no_valid_url), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.empty_clipboard), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateLimitStrings() {
        val limitString1 = "${binding.tvUrlTitle.text.length} / $MAX_TITLE_LENGTH"
        val limitString2 = "${binding.tvUrlDescription.text.length} / $MAX_DESCRIPTION_LENGTH"
        binding.tvLimit1.text = limitString1
        binding.tvLimit2.text = limitString2
    }

    private fun insertHookIntoDB() {
        val url = binding.tvUrlLink.text.toString().trim()
        val title = binding.tvUrlTitle.text.toString().trim()
        val description = binding.tvUrlDescription.text.toString().trim()
        val hookId = DateUtils.generateHookId()

        if (title.isEmpty()) {
            Toast.makeText(this, getString(R.string.plz_input_title), Toast.LENGTH_SHORT).show()
            return
        }

        val hook = Hook(
            hookId = hookId,
            title = title,
            url = UrlUtils.ensureProtocol(url),
            description = description
        )

        hookViewModel.insertHookWithTags(hook, selectedTags)
        finish()
    }

    private fun updateButtonState() {
        val isValid = isUrlValid && isTitleValid
        val finishButton = binding.ivAddNewHook
        finishButton.isEnabled = isValid

        val color = if (isValid) R.color.purple else R.color.gray_100
        finishButton.setBackgroundColor(ContextCompat.getColor(this, color))
    }

    override fun onTagsSelected(tags: List<String>) {
        binding.containerTag.text = tags.joinToString(" ") { "#$it" }
        selectedTags = tags.distinct()
        Log.d(TAG, "onTagsSelected : $tags")
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