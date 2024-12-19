package com.hanto.hook.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hanto.hook.data.model.Hook
import com.hanto.hook.databinding.ItemHomeTagBinding
import com.hanto.hook.ui.view.HookDetailActivity

class TagHomeAdapter(private val tags: List<String?>, private val selectedHook: Hook) :
    RecyclerView.Adapter<TagHomeAdapter.TagViewHolder>() {

    inner class TagViewHolder(val binding: ItemHomeTagBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val textView = binding.tvTagName

        fun bind(tag: String) {
            textView.text = tag
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val binding = ItemHomeTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tags[position]
        tag?.let { holder.bind(it) } // Nullable 체크 후 사용

        // 태그 클릭 이벤트 설정
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            // 클릭한 태그에 관련된 작업 수행
            Intent(context, HookDetailActivity::class.java).apply {
                putExtra("item_title", selectedHook.title)
                putExtra("item_url", selectedHook.url)
                putExtra("item_description", selectedHook.description)

                context.startActivity(this)
            }
        }
    }

    override fun getItemCount(): Int {
        return tags.filterNotNull().size // Nullable 항목 필터링 후 크기 반환
    }
}
