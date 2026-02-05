package com.hanto.hook.ui.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.hanto.hook.databinding.FragmentCommonDialogBinding

class CommonDialogFragment : DialogFragment() {

    enum class DialogType {
        MESSAGE,
        INPUT
    }

    private var _binding: FragmentCommonDialogBinding? = null
    private val binding get() = _binding!!
    private var onPositiveClick: (() -> Unit)? = null
    private var onInputConfirm: ((String) -> Unit)? = null

    companion object {
        const val TAG = "CommonDialogFragment"

        private const val ARG_TYPE = "type"
        private const val ARG_TITLE = "title"
        private const val ARG_CONTENT = "content" // 메시지 또는 hint
        private const val ARG_POS_TEXT = "posText"
        private const val ARG_NEG_TEXT = "negText"
        private const val ARG_PREFILLED_TEXT = "prefilledText" // 수정 시 기존 텍스트

        fun newInstance(
            title: String,
            message: String,
            positiveText: String = "확인",
            negativeText: String = "취소",
            onPositiveClick: () -> Unit
        ): CommonDialogFragment {
            return CommonDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_TYPE, DialogType.MESSAGE)
                    putString(ARG_TITLE, title)
                    putString(ARG_CONTENT, message)
                    putString(ARG_POS_TEXT, positiveText)
                    putString(ARG_NEG_TEXT, negativeText)
                }
                this.onPositiveClick = onPositiveClick
            }
        }

        fun newInputInstance(
            title: String,
            hint: String,
            prefilledText: String = "",
            positiveText: String = "저장",
            negativeText: String = "취소",
            onInputConfirm: (String) -> Unit
        ): CommonDialogFragment {
            return CommonDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_TYPE, DialogType.INPUT)
                    putString(ARG_TITLE, title)
                    putString(ARG_CONTENT, hint)
                    putString(ARG_PREFILLED_TEXT, prefilledText)
                    putString(ARG_POS_TEXT, positiveText)
                    putString(ARG_NEG_TEXT, negativeText)
                }
                this.onInputConfirm = onInputConfirm
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommonDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI 설정
        val type = arguments?.getSerializable(ARG_TYPE) as? DialogType ?: DialogType.MESSAGE
        val title = arguments?.getString(ARG_TITLE)
        val content = arguments?.getString(ARG_CONTENT)
        val posText = arguments?.getString(ARG_POS_TEXT)
        val negText = arguments?.getString(ARG_NEG_TEXT)
        val prefilledText = arguments?.getString(ARG_PREFILLED_TEXT)

        binding.apply {
            tvTitle.text = title
            btnPositive.text = posText
            btnNegative.text = negText

            // 타입에 따라 보여줄 뷰 결정
            if (type == DialogType.MESSAGE) {
                tvMessage.isVisible = true
                etInput.isVisible = false
                tvMessage.text = content // 메시지 내용

                // 확인 버튼 클릭
                btnPositive.setOnClickListener {
                    onPositiveClick?.invoke()
                    dismiss()
                }

            } else {
                tvMessage.isVisible = false
                etInput.isVisible = true
                etInput.hint = content
                etInput.setText(prefilledText)

                // 확인 버튼 클릭 (입력값 전달)
                btnPositive.setOnClickListener {
                    val inputText = etInput.text.toString().trim()
                    if (inputText.isNotEmpty()) {
                        onInputConfirm?.invoke(inputText)
                        dismiss()
                    }
                }
            }

            // 취소 버튼 (공통)
            btnNegative.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}