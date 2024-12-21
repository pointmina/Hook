package com.hanto.hook.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.hanto.hook.data.model.Hook

class HookDiffCallback(
    private val oldList: List<Hook>,
    private val newList: List<Hook>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].hookId == newList[newItemPosition].hookId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
