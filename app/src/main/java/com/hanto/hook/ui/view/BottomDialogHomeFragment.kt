package com.hanto.hook.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hanto.hook.data.model.Hook
import com.hanto.hook.databinding.BottomDialogHomeBinding

class BottomDialogHomeFragment : BottomSheetDialogFragment() {

    private var hook: Hook? = null
    private var onPinClickListener: (() -> Unit)? = null
    private var onModifyClickListener: (() -> Unit)? = null
    private var onDeleteClickListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = BottomDialogHomeBinding.inflate(inflater, container, false)

        hook?.let {
            // 추가적인 초기화 (예: 제목 등)
        }

        // 수정 버튼 클릭 시
        binding.btnSetPin.setOnClickListener {
            onPinClickListener?.invoke()
            dismiss()
        }

        // 수정 버튼 클릭 시
        binding.btnModifyHook.setOnClickListener {
            onModifyClickListener?.invoke()
            dismiss()
        }

        // 삭제 버튼 클릭 시
        binding.btDeleteHook.setOnClickListener {
            onDeleteClickListener?.invoke()
            dismiss()
        }

        return binding.root
    }

    fun setHook(hook: Hook) {
        this.hook = hook
    }

    fun setOnPinClickListener(listener: () -> Unit) {
        this.onPinClickListener = listener
    }

    fun setOnModifyClickListener(listener: () -> Unit) {
        this.onModifyClickListener = listener
    }

    fun setOnDeleteClickListener(listener: () -> Unit) {
        this.onDeleteClickListener = listener
    }
}
