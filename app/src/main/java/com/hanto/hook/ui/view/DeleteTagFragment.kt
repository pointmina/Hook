package com.hanto.hook.ui.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.hanto.hook.databinding.FragmentDeleteTagBinding

class DeleteTagFragment(private val onTagDeleted: () -> Unit) : DialogFragment() {

    private var _binding: FragmentDeleteTagBinding? = null
    private val binding get() = _binding!!

    private var selectedTagId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeleteTagBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun deleteTag(tagId: Int) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // 선택된 태그 ID 가져오기
        selectedTagId = arguments?.getInt("selectedTagId", -1) ?: -1

        // 태그 삭제
        binding.btnDeleteTag.setOnClickListener {
            if (selectedTagId != -1) {
                deleteTag(selectedTagId)
            } else {
                Toast.makeText(requireContext(), "태그 ID가 유효하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
