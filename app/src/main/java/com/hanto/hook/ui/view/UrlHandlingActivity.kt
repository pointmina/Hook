package com.hanto.hook.ui.view

import HookRepository
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.hanto.hook.data.TagSelectionListener
import com.hanto.hook.data.model.Hook
import com.hanto.hook.data.model.Tag
import com.hanto.hook.database.AppDatabase
import com.hanto.hook.database.DatabaseModule
import com.hanto.hook.databinding.ActivityUrlHandlingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UrlHandlingActivity : AppCompatActivity(), TagSelectionListener {

    val TAG = "UrlHandlingActivity"

    private lateinit var binding: ActivityUrlHandlingBinding

    private lateinit var hookRepository: HookRepository
    private lateinit var appDatabase: AppDatabase

    private var selectedTags: List<String> = emptyList()
    private val multiChoiceList = linkedMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        // 뷰 바인딩 초기화
        binding = ActivityUrlHandlingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 데이터베이스 초기화
        appDatabase = DatabaseModule.getDatabase()
        hookRepository = HookRepository(appDatabase)

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
            if (sharedText != null) {
                binding.editTextUrl.setText(sharedText)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun insertHookIntoDB() {
        val title = binding.editTextTitle.text.toString()
        val url = binding.editTextUrl.text.toString()
        val description = binding.editTextDescription.text.toString()
        val hookId = getCurrentTimeAsString()

        val hook = Hook(
            hookId = hookId,
            title = title,
            url = url,
            description = description
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. Hook 삽입
                hookRepository.insertHook(hook)

                // 2. Tag 삽입 (Hook 삽입이 완료된 후 실행)
                if (selectedTags.isEmpty()) {
                    Log.d(TAG, "선택된 태그가 없습니다.")
                } else {
                    selectedTags.forEach { tagName ->
                        val tag = Tag(
                            hookId = hookId,
                            name = tagName
                        )
                        hookRepository.insertTag(tag)
                    }
                }

                Log.d(TAG, "모든 데이터가 성공적으로 삽입되었습니다.")

            } catch (e: Exception) {
                Log.d(TAG, "데이터 삽입 중 오류 발생: ${e.message}")
            } finally {
                finish()
            }
        }
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