package com.hanto.hook.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hanto.hook.R
import com.hanto.hook.data.TagSelectionListener
import com.hanto.hook.data.model.Hook
import com.hanto.hook.data.model.Tag
import com.hanto.hook.databinding.ActivityUrlHandlingBinding
import com.hanto.hook.viewmodel.HookViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UrlHandlingActivity : AppCompatActivity(), TagSelectionListener {

    val TAG = "UrlHandlingActivity"

    private lateinit var binding: ActivityUrlHandlingBinding


    private var selectedTags: List<String> = emptyList()
    private val multiChoiceList = linkedMapOf<String, Boolean>()

    private lateinit var hookViewModel: HookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        // 뷰 바인딩 초기화
        binding = ActivityUrlHandlingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hookViewModel = HookViewModel()

        // Window 크기 조정
        val window = this.window
        window.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)

        handleIncomingIntent(intent)

        // 태그 선택
        binding.tvTag.setOnClickListener {
            val fragment = TagListFragment.newInstance(multiChoiceList)
            fragment.setTagSelectionListener(this)
            fragment.show(supportFragmentManager, "TagListFragment")
        }


        // "취소" 버튼 클릭 시 액티비티 종료
        binding.btnCancel.setOnClickListener {
            finish()
        }

        // "저장하기" 버튼 클릭 시 데이터 저장
        binding.btnCreate.setOnClickListener {
            insertHookIntoDB()
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            sharedText?.let {
                val urlPattern = Regex("""\bhttps?://\S+""")
                val url = urlPattern.find(it)?.value
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
        val url = binding.tvEditUrl.text.toString()
        val description = binding.tvEditDescription.text.toString()
        val hookId = getCurrentTimeAsString()

        if (title.isEmpty()) {
            runOnUiThread {
                Toast.makeText(this, getString(R.string.input_title), Toast.LENGTH_SHORT).show()
            }
            return
        }
        val hook = Hook(
            hookId = hookId,
            title = title,
            url = url,
            description = description
        )

        hookViewModel.insertHook(hook)

        //2. Tag 삽입 (Hook 삽입이 완료된 후 실행)
        if (selectedTags.isEmpty()) {
            Log.d(TAG, "선택된 태그가 없습니다.")
        } else {
            selectedTags.forEach { tagName ->
                val tag = Tag(
                    hookId = hookId,
                    name = tagName
                )
                hookViewModel.insertTag(tag)
            }
        }
        finish()
    }

    override fun onTagsSelected(tags: List<String>) {
        binding.tvTag.text = tags.joinToString(" ") { "#$it" }
        selectedTags = tags.distinct()
        Log.d(TAG, "onTagsSelected : ${tags.toString()}")
    }

    private fun getCurrentTimeAsString(): String {
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        val currentTime = Calendar.getInstance().time
        return dateFormat.format(currentTime)
    }

}