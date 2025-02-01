package com.hanto.hook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.hanto.hook.R
import com.hanto.hook.data.model.ChatMessage
import com.hanto.hook.databinding.ItemChatMeBinding
import com.hanto.hook.databinding.ItemChatOtherBinding

class ChatAdapter : ListAdapter<ChatMessage, ChatAdapter.ChatViewHolder>(
    object : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.message == newItem.message
        }
    }
) {

    companion object {
        const val VIEW_TYPE_USER = 1
        const val VIEW_TYPE_BOT = 2
    }

    inner class ChatViewHolder(private val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chatMessage: ChatMessage) {
            when (binding) {
                is ItemChatMeBinding -> {
                    binding.tvMsgMe.text = chatMessage.message
                    binding.tvMsgMe.setBackgroundResource(R.drawable.bg_user_bubble)
                }

                is ItemChatOtherBinding -> {
                    binding.tvMsgOther.text = chatMessage.message
                    binding.tvMsgOther.setBackgroundResource(R.drawable.bg_bot_bubble)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isUser) {
            VIEW_TYPE_USER
        } else {
            VIEW_TYPE_BOT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val binding =
                    ItemChatMeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ChatViewHolder(binding)
            }

            VIEW_TYPE_BOT -> {
                val binding =
                    ItemChatOtherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ChatViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
