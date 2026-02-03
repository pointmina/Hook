package com.hanto.hook.ui.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanto.hook.R
import com.hanto.hook.data.model.ChatMessage
import com.hanto.hook.databinding.ActivityTutorialBinding
import com.hanto.hook.ui.adapter.ChatAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TutorialActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "TutorialActivity"
        private const val MESSAGE_DELAY = 1500L
        private const val DIALOG_DELAY = 1000L
    }

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
            ChatMessage(getString(R.string.tut_msg1), isUser = false, isDialog = false),
            ChatMessage(getString(R.string.tut_msg2), isUser = false, isDialog = false),
            ChatMessage(getString(R.string.tut_msg3), isUser = false, isDialog = true)
        )

        // Coroutine 사용으로 Deprecated Handler 교체
        lifecycleScope.launch {
            initialMessages.forEachIndexed { index, message ->
                delay(index * MESSAGE_DELAY)
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
            }
        }
    }

    private fun showNextMessages(isPositiveResponse: Boolean) {
        val nextMessages = if (isPositiveResponse) {
            listOf(ChatMessage(getString(R.string.tut_msg4), isUser = false, isDialog = true))
        } else {
            listOf(
                ChatMessage(getString(R.string.tut_msg5), isUser = false, isDialog = false),
                ChatMessage(getString(R.string.tut_msg6), isUser = false, isDialog = true)
            )
        }

        // Coroutine 사용으로 Deprecated Handler 교체
        lifecycleScope.launch {
            nextMessages.forEachIndexed { index, message ->
                delay((index + 1) * MESSAGE_DELAY)
                addChatMessage(message)
                if (index == nextMessages.lastIndex && message.isDialog) {
                    delay(DIALOG_DELAY)
                    if (isPositiveResponse) {
                        showMessageDialog(
                            getString(R.string.tut_msg7),
                            getString(R.string.yes),
                            getString(R.string.yes),
                            "two",
                            "skip"
                        )
                    } else {
                        showMessageDialog(
                            getString(R.string.no),
                            getString(R.string.yes),
                            getString(R.string.yes2),
                            "one",
                            "tutorial"
                        )
                    }
                }
            }
        }
    }

    private fun addChatMessage(message: ChatMessage) {
        val updatedMessages = chatAdapter.currentList.toMutableList().apply { add(message) }
        chatAdapter.submitList(updatedMessages)
        binding.rvChat.scrollToPosition(chatAdapter.itemCount - 1)
    }

    private fun goToTutorialScreen() {
        val intent = Intent(this, OnboardingActivity::class.java)
        startActivity(intent)
        finish()
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

    private fun handleTwoButtonDialog(
        leftMessage: String,
        rightMessage: String,
        buttonAct: String
    ) {
        binding.btnLeft.apply {
            visibility = View.VISIBLE
            text = leftMessage
            setOnClickListener {
                handleUserResponse(leftMessage)
                binding.dialogChat.visibility = View.GONE
                if (buttonAct == "skip") goToTutorialScreen() else showNextMessages(false)
            }
        }

        binding.btnRight.apply {
            visibility = View.VISIBLE
            text = rightMessage
            setOnClickListener {
                handleUserResponse(rightMessage)
                binding.dialogChat.visibility = View.GONE
                if (buttonAct == "skip") goToMainScreen() else showNextMessages(true)
            }
        }
    }

    private fun handleOneButtonDialog(oneMessage: String, buttonAct: String) {
        binding.btnOne.apply {
            visibility = View.VISIBLE
            text = oneMessage
            setOnClickListener {
                handleUserResponse(oneMessage)
                if (buttonAct == "tutorial") goToTutorialScreen() else goToMainScreen()
            }
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

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}