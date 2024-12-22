package com.hanto.hook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hanto.hook.databinding.ItemTagTagBinding

class TagAdapter(
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<TagAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onClick(tagName: String)

    }

    private val tagList = mutableListOf<String>()

    inner class ViewHolder(val binding: ItemTagTagBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onClick(tagList[position])
                }
            }
        }


        fun bind(tagName: String) {
            binding.tvTagNameXl.text = tagName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTagTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tagList[position])
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    fun submitList(newTagList: List<String>) {
        val diffCallback = TagDiffCallback(tagList, newTagList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        tagList.clear()
        tagList.addAll(newTagList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getItem(position: Int): String? {
        return if (position in tagList.indices) tagList[position] else null
    }

    class TagDiffCallback(
        private val oldList: List<String>,
        private val newList: List<String>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
