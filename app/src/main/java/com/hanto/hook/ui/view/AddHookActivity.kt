package com.hanto.hook.ui.view

import HookRepository
import android.content.ClipboardManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.hanto.hook.BaseActivity
import com.hanto.hook.R
import com.hanto.hook.data.TagSelectionListener
import com.hanto.hook.data.model.Hook
import com.hanto.hook.data.model.Tag
import com.hanto.hook.database.AppDatabase
import com.hanto.hook.database.DatabaseModule
import com.hanto.hook.databinding.ActivityAddHookBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Suppress("DEPRECATION")
class AddHookActivity : BaseActivity(), TagSelectionListener {
    private lateinit var binding: ActivityAddHookBinding

    val TAG = "ActivityAddHook"

    private var isUrlValid = false
    private var isTitleValid = false
    private var isExpanded = false

    private lateinit var hookRepository: HookRepository
    private lateinit var appDatabase: AppDatabase

    private var selectedTags: List<String> = emptyList()
    private val multiChoiceList = linkedMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHookBinding.inflate(layoutInflater)
        val view = binding.root

        // 데이터베이스 초기화
        appDatabase = DatabaseModule.getDatabase()
        hookRepository = HookRepository(appDatabase)


        val tags = intent.getStringArrayListExtra("item_tag_list")
        tags?.forEach { tag ->
            multiChoiceList[tag] = true
        }

        setContentView(view)
        updateButtonState()

        binding.ivAppbarBackButton.setOnClickListener {
            finish()
        }

        binding.ivUrlLink.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            if (clipboard.hasPrimaryClip()) {
                val clipData = clipboard.primaryClip
                if (clipData != null && clipData.itemCount > 0) {
                    // 클립보드의 첫 번째 항목의 텍스트 데이터
                    val item = clipData.getItemAt(0)
                    val pasteData = item.text

                    if (pasteData != null && (pasteData.startsWith("http://") || pasteData.startsWith(
                            "https://"
                        ))
                    ) {
                        binding.tvUrlLink.setText(pasteData)
                        Toast.makeText(this, "가장 최근에 복사한 URL을 가져왔어요!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "클립보드에 유효한 URL이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "클립보드가 비어 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        val limitString1 = "${binding.tvUrlTitle.text.length} / 120"
        val limitString2 = "${binding.tvUrlDescription.text.length} / 80"
        binding.tvLimit1.text = limitString1
        binding.tvLimit2.text = limitString2

        //content에 따라 버튼 active
        binding.tvUrlLink.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()
                isUrlValid = input.isNotBlank() && !input.contains(" ")

                if (!isUrlValid) {
                    binding.tvGuide.visibility = View.VISIBLE
                } else {
                    binding.tvGuide.visibility = View.GONE
                    updateButtonState() // 버튼 상태 업데이트
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
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
                    val innerLimitString1 = "${s.length} / 120"
                    binding.tvLimit1.text = innerLimitString1

                    isTitleValid = s.toString().trim().isNotEmpty()
                    if (!isTitleValid) {
                        binding.tvGuideTitle.visibility = View.VISIBLE
                    } else {
                        binding.tvGuideTitle.visibility = View.GONE
                        updateButtonState()
                    }
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
                    val innerLimitString2 = "${s.length} / 80"
                    binding.tvLimit2.text = innerLimitString2
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 태그 선택
        binding.containerTag.setOnClickListener {
            val fragment = TagListFragment.newInstance(multiChoiceList)
            fragment.setTagSelectionListener(this)
            fragment.show(supportFragmentManager, "TagListFragment")
        }


        // 더보기 뷰
        binding.containerLinkInfoEtc.setOnClickListener {
            val tvUrlDescription = binding.tvUrlDescription
            val tvTag = binding.tvTag
            val containerTag = binding.containerTag
            val downArrow = binding.ivDownArrow
            val tvLimit2 = binding.tvLimit2
            toggleExpandCollapse(tvUrlDescription, tvTag, containerTag, downArrow, tvLimit2)
        }

        binding.ivAddNewHook.setOnClickListener {
            insertHookIntoDB()
        }
    }

    private fun insertHookIntoDB() {
        val url = binding.tvUrlLink.text.toString()
        val title = binding.tvUrlTitle.text.toString()
        val description = binding.tvUrlDescription.text.toString()
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
            }
        }
    }


    private fun updateButtonState() {
        val isValid = isUrlValid && isTitleValid
        val finishButton = binding.ivAddNewHook
        finishButton.isEnabled = isValid

        if (isValid) {
            finishButton.setBackgroundColor(getResources().getColor(R.color.purple))
        } else {
            finishButton.setBackgroundColor(getResources().getColor(R.color.gray_100))
        }
    }

    private fun toggleExpandCollapse(
        tvUrlDescription: TextView,
        tvTag: TextView,
        containerTag: TextView,
        downArrow: ImageView,
        tvLimit2: TextView
    ) {
        isExpanded = !isExpanded

        if (isExpanded) {
            tvUrlDescription.visibility = View.INVISIBLE
            tvTag.visibility = View.INVISIBLE
            containerTag.visibility = View.INVISIBLE
            downArrow.setImageResource(R.drawable.ic_up_arrow)
            tvLimit2.visibility = View.INVISIBLE
        } else {
            tvUrlDescription.visibility = View.VISIBLE
            tvTag.visibility = View.VISIBLE
            containerTag.visibility = View.VISIBLE
            downArrow.setImageResource(R.drawable.ic_down_arrow)
            tvLimit2.visibility = View.VISIBLE
        }
    }

    override fun onTagsSelected(tags: List<String>) {
        binding.containerTag.text = tags.joinToString(" ") { "#$it" }
        selectedTags = tags.distinct()
        Log.d(TAG, "onTagsSelected : ${tags.toString()}")
    }

    private fun getCurrentTimeAsString(): String {
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        val currentTime = Calendar.getInstance().time
        return dateFormat.format(currentTime)
    }

}