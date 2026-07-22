package com.hanto.hook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hanto.hook.databinding.ItemHomeTagBinding

class TagHomeAdapter(private var tags: List<String> = emptyList()) :
    RecyclerView.Adapter<TagHomeAdapter.TagViewHolder>() {

    fun updateTags(newTags: List<String>) {
        tags = newTags
        notifyDataSetChanged()
    }

    inner class TagViewHolder(val binding: ItemHomeTagBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val textView = binding.tvTagName

        fun bind(tagName: String) {
            textView.text = tagName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val binding = ItemHomeTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.bind(tags[position])
    }

    override fun getItemCount(): Int {
        return tags.size
    }
}
