package com.hanto.hook.ui.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hanto.hook.databinding.FragmentDeleteTagBinding
import com.hanto.hook.viewmodel.HookViewModel

class DeleteTagFragment : DialogFragment() {

    val TAG = "DeleteTagFragment"

    interface OnTagDeletedListener {
        fun onTagDeleted()
    }

    private var listener: OnTagDeletedListener? = null

    fun setOnTagDeletedListener(listener: OnTagDeletedListener) {
        this.listener = listener
    }

    private var _binding: FragmentDeleteTagBinding? = null
    private val binding get() = _binding!!

    private lateinit var hookViewModel: HookViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeleteTagBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hookViewModel = HookViewModel()

        val selectedTagName = arguments?.getString("selectedTag")

        // 태그 삭제
        binding.btnDeleteTag.setOnClickListener {
            if (selectedTagName != null) {
                deleteTag(selectedTagName)
            }

        }

    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        _binding = null
    }

    private fun deleteTag(tagName: String) {
        Log.d(TAG, "deleteTag: $tagName")

        hookViewModel.deleteTagByTagName(tagName)

        listener?.onTagDeleted()
        dismiss()
    }
}
