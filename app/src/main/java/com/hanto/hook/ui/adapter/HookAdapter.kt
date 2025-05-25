package com.hanto.hook.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hanto.hook.data.model.Hook
import com.hanto.hook.data.model.Tag
import com.hanto.hook.databinding.ItemHookBinding
import com.hanto.hook.viewmodel.HookViewModel

class HookAdapter(
    private var hooks: MutableList<Hook>,
    private val hookViewModel: HookViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val onItemClickListener: OnItemClickListener,
    private val onItemClick: (Hook) -> Unit,
) : RecyclerView.Adapter<HookAdapter.ViewHolder>() {

    private var filteredHooks: MutableList<Hook> = hooks.toMutableList()
    private val tagsCache = mutableMapOf<String, List<Tag>>()

    interface OnItemClickListener {
        fun onClick(hook: Hook)
        fun onOptionButtonClick(position: Int)
    }

    inner class ViewHolder(private val binding: ItemHookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val tagRecyclerView: RecyclerView = binding.rvTagContainer
        private var currentHookId: String? = null

        fun bind(hook: Hook) {
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

            bindTags(hook.hookId)
        }

        private fun bindTags(hookId: String) {
            if (currentHookId == hookId && tagsCache.containsKey(hookId)) {
                setupTagRecyclerView(tagsCache[hookId] ?: emptyList())
                return
            }

            currentHookId = hookId

            hookViewModel.getTagsForHook(hookId).observe(lifecycleOwner) { tags ->
                if (currentHookId == hookId) {
                    val distinctSortedTags = tags.distinctBy { it.name }.sortedBy { it.name }
                    tagsCache[hookId] = distinctSortedTags
                    setupTagRecyclerView(distinctSortedTags)
                }
            }
        }

        private fun setupTagRecyclerView(tags: List<Tag>) {
            val tagAdapter = TagHomeAdapter(tags)
            tagRecyclerView.layoutManager = FlexboxLayoutManager(binding.root.context).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.FLEX_START
            }
            tagRecyclerView.adapter = tagAdapter
        }
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition in filteredHooks.indices && toPosition in filteredHooks.indices) {
            val movedItem = filteredHooks.removeAt(fromPosition)
            filteredHooks.add(toPosition, movedItem)
            notifyItemMoved(fromPosition, toPosition)
        }
    }

    fun updateHooks(newHooks: List<Hook>, onComplete: (() -> Unit)? = null) {
        val diffCallback = HookDiffCallback(hooks, newHooks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        hooks = newHooks.toMutableList()

        val newHookIds = newHooks.map { it.hookId }.toSet()
        tagsCache.keys.retainAll(newHookIds)

        filter("")
        diffResult.dispatchUpdatesTo(this)
        onComplete?.invoke()
    }

    fun getItem(position: Int): Hook {
        return filteredHooks[position]
    }

    private fun filter(query: String) {
        filteredHooks = if (query.isBlank()) {
            hooks.toMutableList()
        } else {
            hooks.filter { hook ->
                hook.title.contains(query, ignoreCase = true) ||
                        (hook.description?.contains(query, ignoreCase = true) ?: false)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    fun filterHooks(query: String) {
        filter(query)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return filteredHooks.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hook = filteredHooks[position]
        holder.bind(hook)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.itemView.setOnClickListener(null)
    }
}