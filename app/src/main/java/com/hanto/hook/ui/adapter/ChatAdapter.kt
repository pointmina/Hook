package com.hanto.hook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hanto.hook.R
import com.hanto.hook.data.model.ChatMessage
import com.hanto.hook.databinding.ItemChatMeBinding
import com.hanto.hook.databinding.ItemChatOtherBinding

class ChatAdapter : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(ChatDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_BOT = 2
    }

    private class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.message == newItem.message && oldItem.isUser == newItem.isUser
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }

    inner class UserMessageViewHolder(private val binding: ItemChatMeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatMessage: ChatMessage) {
            binding.tvMsgMe.text = chatMessage.message
            binding.tvMsgMe.setBackgroundResource(R.drawable.bg_user_bubble)
        }
    }

    inner class BotMessageViewHolder(private val binding: ItemChatOtherBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatMessage: ChatMessage) {
            binding.tvMsgOther.text = chatMessage.message
            binding.tvMsgOther.setBackgroundResource(R.drawable.bg_bot_bubble)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isUser) {
            VIEW_TYPE_USER
        } else {
            VIEW_TYPE_BOT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val binding = ItemChatMeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                UserMessageViewHolder(binding)
            }
            VIEW_TYPE_BOT -> {
                val binding = ItemChatOtherBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                BotMessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatMessage = getItem(position)
        when (holder) {
            is UserMessageViewHolder -> holder.bind(chatMessage)
            is BotMessageViewHolder -> holder.bind(chatMessage)
            else -> throw IllegalArgumentException("Unknown ViewHolder type")
        }
    }
}