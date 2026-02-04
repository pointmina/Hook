package com.hanto.hook.ui.view.fragment

import android.os.Bundle
import android.text.Editable
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
import com.hanto.hook.R
import com.hanto.hook.data.TagUpdateListener
import com.hanto.hook.databinding.FragmentChangeTagBinding
import com.hanto.hook.viewmodel.HookViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangeTagFragment : DialogFragment() {

    companion object {
        private const val TAG = "ChangeTagFragment"
    }

    private var _binding: FragmentChangeTagBinding? = null
    private val binding get() = _binding!!

    private var tagUpdateListener: TagUpdateListener? = null

    private val hookViewModel: HookViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeTagBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        val selectedTagName = arguments?.getString("selectedTag")

        binding.tvChangeTagName.text =
            selectedTagName?.let { Editable.Factory.getInstance().newEditable(it) }

        binding.btnChangeTagName.setOnClickListener {
            val newTagName = binding.tvChangeTagName.text.toString().trim()
            if (newTagName.isNotEmpty() && selectedTagName != null) {
                updateTagName(selectedTagName, newTagName)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.plz_input_tag),
                    Toast.LENGTH_SHORT
                ).show()
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
                        binding.btnChangeTagName.isEnabled = !isLoading
                    }
                }
            }
        }
    }

    private fun updateTagName(oldTagName: String, newTagName: String) {
        Log.d(TAG, "updateTagName: $oldTagName -> $newTagName")

        hookViewModel.updateTagName(oldTagName, newTagName)
        tagUpdateListener?.onTagUpdated(newTagName)
        dismiss()
    }

    fun setTagUpdateListener(listener: TagUpdateListener) {
        tagUpdateListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}