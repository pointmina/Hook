package com.hanto.hook.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hanto.hook.data.model.Hook
import com.hanto.hook.data.model.HookWithTags
import com.hanto.hook.data.model.Tag
import com.hanto.hook.databinding.ItemHookBinding

class HookAdapter(
    private var hooks: MutableList<HookWithTags>,
    private val onItemClickListener: OnItemClickListener,
    private val onItemClick: (Hook) -> Unit,
) : RecyclerView.Adapter<HookAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onClick(hook: Hook)
        fun onOptionButtonClick(position: Int)
    }

    inner class ViewHolder(private val binding: ItemHookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val tagRecyclerView: RecyclerView = binding.rvTagContainer

        fun bind(hookWithTags: HookWithTags) {
            val hook = hookWithTags.hook
            val tags = hookWithTags.tags

            binding.tvTitle.text = hook.title
            binding.tvUrlLink.text = hook.url

            if (!hook.description.isNullOrBlank()) {
                binding.tvTagDescription.visibility = View.VISIBLE
                binding.tvTagDescription.text = hook.description
            } else {
                binding.tvTagDescription.visibility = View.GONE
            }

            binding.iconIsPinned.visibility = if (hook.isPinned) View.VISIBLE else View.GONE

            binding.root.setOnClickListener { onItemClick(hook) }
            binding.icOption.setOnClickListener {
                onItemClickListener.onOptionButtonClick(bindingAdapterPosition)
            }

            setupTagRecyclerView(tags)
        }

        private fun setupTagRecyclerView(tags: List<Tag>) {
            val sortedTags = tags.distinctBy { it.name }.sortedBy { it.name }

            val tagAdapter = TagHomeAdapter(sortedTags)
            tagRecyclerView.layoutManager = FlexboxLayoutManager(binding.root.context).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.FLEX_START
            }
            tagRecyclerView.adapter = tagAdapter
        }
    }

    fun updateHooks(newHooks: List<HookWithTags>, onComplete: (() -> Unit)? = null) {
        val diffCallback = HookDiffCallback(hooks, newHooks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        hooks = newHooks.toMutableList()
        diffResult.dispatchUpdatesTo(this)
        onComplete?.invoke()
    }

    fun getItem(position: Int): Hook {
        return hooks[position].hook
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = hooks.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(hooks[position])
    }
}