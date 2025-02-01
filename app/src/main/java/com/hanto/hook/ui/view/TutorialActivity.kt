package com.hanto.hook.ui.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanto.hook.R
import com.hanto.hook.data.model.ChatMessage
import com.hanto.hook.databinding.ActivityTutorialBinding
import com.hanto.hook.ui.adapter.ChatAdapter

class TutorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTutorialBinding
    private lateinit var chatAdapter: ChatAdapter

    private var userResponse: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        loadChatMessages()

    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        binding.rvChat.apply {
            layoutManager = LinearLayoutManager(this@TutorialActivity)
            adapter = chatAdapter
        }
    }

    private fun loadChatMessages() {
        val initialMessages = listOf(
            ChatMessage("안녕하세요!😊 ", isUser = false, isDialog = false),
            ChatMessage("Hook에 오신 것을 환영합니다", isUser = false, isDialog = false),
            ChatMessage("Hook을 사용해보신 적이 있으신가요?", isUser = false, isDialog = true)
        )

        initialMessages.forEachIndexed { index, message ->
            Handler(Looper.getMainLooper()).postDelayed({
                addChatMessage(message)
                if (message.isDialog) {
                    showMessageDialog(
                        getString(R.string.no),
                        getString(R.string.yes),
                        "",
                        "two",
                        "first"
                    )
                }
            }, index * 1500L)
        }
    }

    private fun showNextMessages(isPositiveResponse: Boolean) {
        val nextMessages = if (isPositiveResponse) {
            listOf(ChatMessage("좋아요! 그럼 튜토리얼을 건너뛸까요?", isUser = false, isDialog = true))
        } else {
            listOf(
                ChatMessage("그렇군요!", isUser = false, isDialog = false),
                ChatMessage("Hook을 편리하게 사용할 수 있는 방법을 알려 드릴게요!", isUser = false, isDialog = true)
            )
        }

        nextMessages.forEachIndexed { index, message ->
            Handler(Looper.getMainLooper()).postDelayed({
                addChatMessage(message)
                if (index == nextMessages.lastIndex && message.isDialog) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (isPositiveResponse) {
                            showMessageDialog("그래도 다시 설명해줘!", "응!", "네", "two", "skip")
                        } else {
                            showMessageDialog(
                                getString(R.string.no),
                                getString(R.string.yes),
                                "좋아!",
                                "one",
                                "tutorial"
                            )
                        }
                    }, 1000L)
                }
            }, (index + 1) * 1500L)
        }
    }

    private fun addChatMessage(message: ChatMessage) {
        val updatedMessages = chatAdapter.currentList.toMutableList().apply { add(message) }
        chatAdapter.submitList(updatedMessages)
        binding.rvChat.scrollToPosition(chatAdapter.itemCount - 1)
    }



    private fun goToTutorialScreen() {
        Intent(this, OnboardingActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    private fun showMessageDialog(
        leftMessage: String,
        rightMessage: String,
        oneMessage: String,
        buttonType: String,
        buttonAct: String
    ) {
        binding.dialogChat.visibility = View.VISIBLE
        resetButtons()

        when (buttonType) {
            "two" -> handleTwoButtonDialog(leftMessage, rightMessage, buttonAct)
            "one" -> handleOneButtonDialog(oneMessage, buttonAct)
        }
    }

    private fun resetButtons() {
        binding.btnOne.visibility = View.GONE
        binding.btnLeft.visibility = View.GONE
        binding.btnRight.visibility = View.GONE
    }

    private fun handleTwoButtonDialog(leftMessage: String, rightMessage: String, buttonAct: String) {
        binding.btnLeft.visibility = View.VISIBLE
        binding.btnRight.visibility = View.VISIBLE
        binding.btnLeft.text = leftMessage
        binding.btnRight.text = rightMessage

        binding.btnLeft.setOnClickListener {
            handleUserResponse(leftMessage)
            binding.dialogChat.visibility = View.GONE
            if (buttonAct == "skip") goToTutorialScreen() else showNextMessages(false)
        }

        binding.btnRight.setOnClickListener {
            handleUserResponse(rightMessage)
            binding.dialogChat.visibility = View.GONE
            if (buttonAct == "skip") goToMainScreen() else showNextMessages(true)
        }
    }

    private fun handleOneButtonDialog(oneMessage: String, buttonAct: String) {
        binding.btnOne.visibility = View.VISIBLE
        binding.btnOne.text = oneMessage

        binding.btnOne.setOnClickListener {
            handleUserResponse(oneMessage)
            if (buttonAct == "tutorial") goToTutorialScreen() else goToMainScreen()
        }
    }

    private fun handleUserResponse(message: String) {
        userResponse = message
        val userMessage = ChatMessage(userResponse!!, isUser = true, isDialog = false)
        val updatedMessages = chatAdapter.currentList.toMutableList().apply { add(userMessage) }
        chatAdapter.submitList(updatedMessages)
    }


    private fun goToMainScreen() {
        val sharedPref = getSharedPreferences("hook_prefs", MODE_PRIVATE)
        sharedPref.edit().putBoolean("isFirstLaunch", false).apply()
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
