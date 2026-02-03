package com.hanto.hook.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.hanto.hook.data.model.HookWithTags

class HookDiffCallback(
    private val oldList: List<HookWithTags>,
    private val newList: List<HookWithTags>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].hook.hookId == newList[newItemPosition].hook.hookId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}