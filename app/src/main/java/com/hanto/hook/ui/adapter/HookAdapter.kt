package com.hanto.hook.ui.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hanto.hook.data.Hook
import com.hanto.hook.data.Tag
import com.hanto.hook.databinding.ItemHookBinding

class HookAdapter(
    private var hooks: ArrayList<Hook>,
    private var tag: List<Tag>,
) : RecyclerView.Adapter<HookAdapter.ViewHolder>() {



    inner class ViewHolder(val binding: ItemHookBinding) : RecyclerView.ViewHolder(binding.root) {

        init {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hook = hooks[position]
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return hooks.size
    }

    fun getItem(position: Int): Hook {
        return hooks[position]
    }

    fun updateData() {
    }
}
