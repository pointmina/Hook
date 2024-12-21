package com.hanto.hook.ui.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hanto.hook.data.model.Hook
import com.hanto.hook.databinding.ItemHookBinding
import com.hanto.hook.viewmodel.HookViewModel

class HookAdapter(
    private var hooks: List<Hook>,
    private val hookViewModel: HookViewModel,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<HookAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemHookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val tagRecyclerView: RecyclerView =
            binding.rvTagContainer // rv_tag_container 리사이클러뷰

        fun bind(hook: Hook) {
            binding.tvTitle.text = hook.title
            binding.tvUrlLink.text = hook.url
            binding.tvTagDescription.text = hook.description


            hookViewModel.getTagsForHook(hook.hookId)?.observe(lifecycleOwner, Observer { tags ->
                val tagAdapter = TagHomeAdapter(tags)
                tagRecyclerView.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false) 
                tagRecyclerView.adapter = tagAdapter
            })

        }
    }

    fun updateHooks(newHooks: List<Hook>) {
        val diffCallback = HookDiffCallback(hooks, newHooks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        hooks = newHooks
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hook = hooks[position]
        holder.bind(hook)
    }

    override fun getItemCount(): Int {
        return hooks.size
    }

    fun getItem(position: Int): Hook {
        return hooks[position]
    }
}
