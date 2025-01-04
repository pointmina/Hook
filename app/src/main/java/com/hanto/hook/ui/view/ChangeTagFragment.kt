package com.hanto.hook.ui.view

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.hanto.hook.data.TagUpdateListener
import com.hanto.hook.databinding.FragmentChangeTagBinding
import com.hanto.hook.viewmodel.HookViewModel


class ChangeTagFragment : DialogFragment() {

    private val TAG = "ChangeTagFragment"

    private var _binding: FragmentChangeTagBinding? = null
    private val binding get() = _binding!!

    private var tagUpdateListener: TagUpdateListener? = null
    private lateinit var hookViewModel: HookViewModel


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

        hookViewModel = HookViewModel()

        val selectedTagName = arguments?.getString("selectedTag")

        binding.tvChangeTagName.text =
            selectedTagName?.let { Editable.Factory.getInstance().newEditable(it) }

        binding.btnChangeTagName.setOnClickListener {
            val newTagName = binding.tvChangeTagName.text.toString()
            if (newTagName.isNotEmpty() && selectedTagName != null) {
                updateTagName(selectedTagName, newTagName)
            } else {
                Toast.makeText(requireContext(), "태그 이름을 입력하세요.", Toast.LENGTH_SHORT).show()
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

