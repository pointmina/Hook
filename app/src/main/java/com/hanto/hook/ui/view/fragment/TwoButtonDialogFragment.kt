package com.hanto.hook.ui.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hanto.hook.databinding.FragmentTwoButtonDialogBinding

class TwoButtonDialogFragment(
    private val title: String,
    private val content: String,
    private val positiveButtonText: String = "예",
    private val negativeButtonText: String = "아니요",
    private val onPositiveClick: (() -> Unit)? = null
) : DialogFragment() {

    private var _binding: FragmentTwoButtonDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTwoButtonDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDialogTwoTitle.text = title
        binding.tvDialogTwoContent.text = content
        binding.btnYes.text = positiveButtonText
        binding.btnNo.text = negativeButtonText

        binding.btnYes.setOnClickListener {
            onPositiveClick?.invoke()
            dismiss()
        }

        binding.btnNo.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
