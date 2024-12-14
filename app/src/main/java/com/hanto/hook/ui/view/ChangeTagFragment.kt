package com.hanto.hook.ui.view

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.hanto.hook.databinding.FragmentChangeTagBinding
import com.hanto.hook.viewmodel.HookViewModel


class ChangeTagFragment(private val onTagUpdated: (String) -> Unit) : DialogFragment() {

    private var _binding: FragmentChangeTagBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HookViewModel

    private var selectedTagId: Int = -1


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeTagBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val selectedTag = arguments?.getString("selectedTag")
        selectedTagId = arguments?.getInt("selectedTagId", -1) ?: -1

        binding.tvChangeTagName.text =
            selectedTag?.let { Editable.Factory.getInstance().newEditable(it) }


        binding.btnChangeTagName.setOnClickListener {
            val newTagName = binding.tvChangeTagName.text.toString()
            if (newTagName.isNotEmpty() && selectedTagId != -1) {
                updateTagName(selectedTagId, newTagName)
            } else {
                Toast.makeText(requireContext(), "태그 이름을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        val changeTagName = binding.tvChangeTagName
        changeTagName.setOnClickListener {
            showKeyboardAndFocus(changeTagName)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showKeyboardAndFocus(editText: EditText) {
        editText.requestFocus()
        val imm =
            activity?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun updateTagName(tagId: Int, newTagName: String) {

    }
}
