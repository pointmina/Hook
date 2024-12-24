package com.hanto.hook.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hanto.hook.data.model.Hook
import com.hanto.hook.databinding.ItemHookBinding
import com.hanto.hook.viewmodel.HookViewModel

class HookAdapter(
    private var hooks: List<Hook>,
    private val hookViewModel: HookViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val onItemClickListener: OnItemClickListener,
    private val onItemClick: (Hook) -> Unit,
) : RecyclerView.Adapter<HookAdapter.ViewHolder>() {

    private var filteredHooks: List<Hook> = hooks


    interface OnItemClickListener {
        fun onClick(hook: Hook)  // Hook 객체를 직접 넘김
        fun onOptionButtonClick(position: Int)  // position 유지
    }

    inner class ViewHolder(private val binding: ItemHookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val tagRecyclerView: RecyclerView =
            binding.rvTagContainer

        fun bind(hook: Hook) {
            binding.tvTitle.text = hook.title
            binding.tvUrlLink.text = hook.url
            if (!hook.description.isNullOrBlank()) {
                binding.tvTagDescription.visibility = View.VISIBLE
                binding.tvTagDescription.text = hook.description
            } else {
                binding.tvTagDescription.visibility = View.GONE
            }

            // 아이템 클릭 시 Hook 객체를 전달
            binding.root.setOnClickListener {
                onItemClick(hook)
            }

            // 옵션 버튼 클릭 시 position 전달
            binding.icOption.setOnClickListener {
                onItemClickListener.onOptionButtonClick(adapterPosition)
            }

            hookViewModel.getTagsForHook(hook.hookId)?.observe(lifecycleOwner, Observer { tags ->
                // 중복 제거 및 정렬
                val distinctSortedTags = tags
                    .distinctBy { it.name }
                    .sortedBy { it.name }

                val tagAdapter = TagHomeAdapter(distinctSortedTags)
                tagRecyclerView.layoutManager = FlexboxLayoutManager(binding.root.context).apply {
                    flexDirection = FlexDirection.ROW
                    justifyContent = JustifyContent.FLEX_START
                }
                tagRecyclerView.adapter = tagAdapter
            })

        }
    }

    fun updateHooks(newHooks: List<Hook>, onComplete: (() -> Unit)? = null) {
        hooks = newHooks
        filter("") // 필터링 초기화
        onComplete?.invoke()
    }

    fun getItem(position: Int): Hook {
        return filteredHooks[position]
    }

    fun filter(query: String) {
        filteredHooks = if (query.isBlank()) {
            hooks
        } else {
            hooks.filter { hook ->
                hook.title.contains(query, ignoreCase = true) ||
                        (hook.description?.contains(query, ignoreCase = true) ?: false)
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val hook = hooks[position]
//        holder.bind(hook)
//    }

    override fun getItemCount(): Int {
        return filteredHooks.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hook = filteredHooks[position]
        holder.bind(hook)
    }

//    fun getItem(position: Int): Hook {
//        return hooks[position]
//    }
}
