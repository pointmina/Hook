package com.hanto.hook.ui.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hanto.hook.R
import com.hanto.hook.data.TagSelectionListener
import com.hanto.hook.data.model.Hook
import com.hanto.hook.databinding.ActivityUrlHandlingBinding
import com.hanto.hook.ui.view.fragment.TagListFragment
import com.hanto.hook.util.UrlUtils
import com.hanto.hook.viewmodel.HookViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UrlHandlingActivity : AppCompatActivity(), TagSelectionListener {

    companion object {
        private const val TAG = "UrlHandlingActivity"
        private const val WINDOW_WIDTH_RATIO = 0.9
    }

    private lateinit var binding: ActivityUrlHandlingBinding

    private var selectedTags: List<String> = emptyList()
    private val multiChoiceList = linkedMapOf<String, Boolean>()

    private val hookViewModel: HookViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        binding = ActivityUrlHandlingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindow()
        setupViews()
        setupObservers()
        handleIncomingIntent(intent)
    }

    private fun setupWindow() {
        val window = this.window
        window.setLayout(
            (resources.displayMetrics.widthPixels * WINDOW_WIDTH_RATIO).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun setupViews() {
        // 태그 선택
        binding.tvTag.setOnClickListener {
            val fragment = TagListFragment.newInstance(multiChoiceList)
            fragment.setTagSelectionListener(this)
            fragment.show(supportFragmentManager, "TagListFragment")
        }

        // 취소
        binding.btnCancel.setOnClickListener {
            finish()
        }

        // 저장하기
        binding.btnCreate.setOnClickListener {
            insertHookIntoDB()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // 1. 에러 메시지 관찰
                launch {
                    hookViewModel.errorMessage.collect { errorMessage ->
                        errorMessage?.let {
                            Toast.makeText(this@UrlHandlingActivity, it, Toast.LENGTH_SHORT).show()
                            hookViewModel.clearErrorMessage()
                        }
                    }
                }

                // 2. 로딩 상태 관찰 (저장 버튼 활성화 제어)
                launch {
                    hookViewModel.isLoading.collect { isLoading ->
                        binding.btnCreate.isEnabled = !isLoading
                    }
                }
            }
        }
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            sharedText?.let {
                val url = UrlUtils.extractUrlFromText(it)
                if (url != null) {
                    binding.tvEditUrl.text = url
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun insertHookIntoDB() {
        val title = binding.tvEditTitle.text.toString().trim()
        val url = binding.tvEditUrl.text.toString().trim()
        val description = binding.tvEditDescription.text.toString().trim()

        val hookId = java.util.UUID.randomUUID().toString()

        if (title.isEmpty()) {
            Toast.makeText(this, getString(R.string.input_title), Toast.LENGTH_SHORT).show()
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

    override fun onTagsSelected(tags: List<String>) {
        binding.tvTag.text = tags.joinToString(" ") { "#$it" }
        selectedTags = tags.distinct()
        Log.d(TAG, "onTagsSelected : $tags")
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }
}