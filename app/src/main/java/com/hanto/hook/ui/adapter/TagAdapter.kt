package com.hanto.hook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hanto.hook.databinding.ItemTagTagBinding

class TagAdapter(
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<TagAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onClick(position: Int)
    }

    inner class ViewHolder(val binding: ItemTagTagBinding) :
        RecyclerView.ViewHolder(binding.root) {


        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTagTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 1
    }

    fun getItem(position: Int) {

    }
}