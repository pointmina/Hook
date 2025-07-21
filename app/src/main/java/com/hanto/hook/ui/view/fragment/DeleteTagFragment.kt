package com.hanto.hook.ui.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.hanto.hook.databinding.FragmentDeleteTagBinding
import com.hanto.hook.viewmodel.HookViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteTagFragment : DialogFragment() {

    companion object {
        private const val TAG = "DeleteTagFragment"
    }

    interface OnTagDeletedListener {
        fun onTagDeleted()
    }

    private var listener: OnTagDeletedListener? = null

    fun setOnTagDeletedListener(listener: OnTagDeletedListener) {
        this.listener = listener
    }

    private var _binding: FragmentDeleteTagBinding? = null
    private val binding get() = _binding!!

    private val hookViewModel: HookViewModel by viewModels()

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

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        val selectedTagName = arguments?.getString("selectedTag")

        binding.btnDeleteTag.setOnClickListener {
            if (selectedTagName != null) {
                deleteTag(selectedTagName)
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setupObservers() {
        // 에러 메시지 관찰
        hookViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                hookViewModel.clearErrorMessage()
            }
        }

        // 로딩 상태 관찰
        hookViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnDeleteTag.isEnabled = !isLoading
        }
    }

    private fun deleteTag(tagName: String) {
        Log.d(TAG, "deleteTag: $tagName")

        hookViewModel.deleteTagByTagName(tagName)
        listener?.onTagDeleted()
        dismiss()
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        _binding = null
    }
}