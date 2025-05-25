package com.hanto.hook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hanto.hook.databinding.ItemTagBinding

class TagListAdapter(
    private val tagMap: MutableMap<String, Boolean> // Context 제거
) : RecyclerView.Adapter<TagListAdapter.TagViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        // parent.context 사용으로 Context 문제 해결
        val binding = ItemTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tagMap.keys.elementAt(position)
        holder.bind(tag, tagMap[tag] ?: false)
    }

    override fun getItemCount(): Int {
        return tagMap.size
    }

    inner class TagViewHolder(private val binding: ItemTagBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val tag = tagMap.keys.elementAt(position)
                    tagMap[tag] = isChecked
                }
            }

            // 사용자 편의를 위해 체크 선택 범위를 넓힘
            binding.tvTagName.setOnClickListener {
                val currentCheckState = binding.checkbox.isChecked
                binding.checkbox.isChecked = !currentCheckState
            }
        }

        fun bind(tag: String, isChecked: Boolean) {
            binding.tvTagName.text = tag
            binding.checkbox.isChecked = isChecked
        }
    }
}