package com.hanto.hook.ui.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hanto.hook.databinding.FragmentDeleteTagBinding
import com.hanto.hook.viewmodel.HookViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // 1. 에러 메시지 관찰
                launch {
                    hookViewModel.errorMessage.collect { errorMessage ->
                        errorMessage?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                            hookViewModel.clearErrorMessage()
                        }
                    }
                }

                // 2. 로딩 상태 관찰 (버튼 활성화 제어)
                launch {
                    hookViewModel.isLoading.collect { isLoading ->
                        binding.btnDeleteTag.isEnabled = !isLoading
                    }
                }
            }
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