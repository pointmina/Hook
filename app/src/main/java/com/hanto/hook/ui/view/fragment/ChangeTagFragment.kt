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
import com.hanto.hook.R
import com.hanto.hook.data.TagUpdateListener
import com.hanto.hook.databinding.FragmentChangeTagBinding
import com.hanto.hook.viewmodel.HookViewModel
import dagger.hilt.android.AndroidEntryPoint

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
        // 에러 메시지 관찰
        hookViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                hookViewModel.clearErrorMessage()
            }
        }

        // 로딩 상태 관찰
        hookViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnChangeTagName.isEnabled = !isLoading
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