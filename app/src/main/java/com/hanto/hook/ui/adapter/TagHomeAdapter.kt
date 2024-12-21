package com.hanto.hook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hanto.hook.data.model.Tag
import com.hanto.hook.databinding.ItemHomeTagBinding

class TagHomeAdapter(private var tags: List<Tag>) :
    RecyclerView.Adapter<TagHomeAdapter.TagViewHolder>() {

    inner class TagViewHolder(val binding: ItemHomeTagBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val textView = binding.tvTagName

        fun bind(tag: Tag) {
            textView.text = tag.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val binding = ItemHomeTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tags[position]
        holder.bind(tag)
    }

    override fun getItemCount(): Int {
        return tags.size
    }

    fun updateTags(newTags: List<Tag>) {
        tags = newTags
        notifyDataSetChanged()
    }
}
