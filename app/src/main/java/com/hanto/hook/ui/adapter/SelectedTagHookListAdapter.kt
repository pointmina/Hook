package com.hanto.hook.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hanto.hook.data.model.Hook
import com.hanto.hook.databinding.ItemSelectedTagHookListBinding

class SelectedTagHookListAdapter(
    private var hooks: List<Hook> = listOf(),
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<SelectedTagHookListAdapter.SelectedTagHookViewHolder>() {

    interface OnItemClickListener {
        fun onClick(position: Int)
        fun onOptionButtonClick(position: Int)
    }

    fun submitList(newHooks: List<Hook>) {
        Log.d("SelectedTagHookListAdapter", "submitList called with size: ${newHooks.size}")
        val diffCallback = HookDiffCallback(hooks, newHooks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        hooks = newHooks
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedTagHookViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSelectedTagHookListBinding.inflate(inflater, parent, false)
        return SelectedTagHookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectedTagHookViewHolder, position: Int) {
        hooks.getOrNull(position)?.let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int = hooks.size

    fun getItem(position: Int): Hook? = hooks.getOrNull(position)

    // 뷰홀더 클래스
    inner class SelectedTagHookViewHolder(private val binding: ItemSelectedTagHookListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onClick(position)
                }
            }
        }

        fun bind(hook: Hook) {
            with(binding) {
                tvTitle.text = hook.title
                tvUrlLink.text = hook.url
                tvTagDescription.text = hook.description

                if (!hook.description.isNullOrEmpty()) {
                    tvTagDescription.visibility = View.VISIBLE
                    tvTagDescription.text = hook.description
                } else {
                    tvTagDescription.visibility = View.GONE
                }

                if (hook.isPinned) {
                    binding.iconIsPinned.visibility = View.VISIBLE
                } else {
                    binding.iconIsPinned.visibility = View.GONE
                }

                root.setOnClickListener {
                    listener.onClick(adapterPosition)
                }

                icOption.setOnClickListener {
                    listener.onOptionButtonClick(adapterPosition)
                }

                btOption.setOnClickListener {
                    listener.onOptionButtonClick(adapterPosition)
                }

            }
        }
    }

}
