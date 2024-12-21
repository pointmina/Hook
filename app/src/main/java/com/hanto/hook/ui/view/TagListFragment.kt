package com.hanto.hook.ui.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanto.hook.data.TagSelectionListener
import com.hanto.hook.databinding.FragmentTagListBinding
import com.hanto.hook.ui.adapter.TagListAdapter

class TagListFragment : DialogFragment() {

    private var tagSelectionListener: TagSelectionListener? = null
    private var _binding: FragmentTagListBinding? = null
    private val binding get() = _binding!!
    private lateinit var multiChoiceList: LinkedHashMap<String, Boolean>
    private lateinit var adapter: TagListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTagListBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            multiChoiceList =
                it.getSerializable("multiChoiceList") as LinkedHashMap<String, Boolean>
        }

        adapter = TagListAdapter(requireContext(), multiChoiceList)
        binding.lvTags.adapter = adapter
        binding.lvTags.layoutManager = LinearLayoutManager(requireContext())

        // btn_add_tag 클릭 리스너 설정
        binding.btnAddTag.setOnClickListener {
            val newTag = binding.tvAddNewTag.text.toString().trim()
            if (newTag.isEmpty()) {
                Toast.makeText(requireContext(), "태그를 입력하세요.", Toast.LENGTH_SHORT).show()
            } else if (multiChoiceList.containsKey(newTag)) {
                Toast.makeText(requireContext(), "이미 존재하는 태그입니다.", Toast.LENGTH_SHORT).show()
            } else {
                multiChoiceList[newTag] = true
                binding.lvTags.adapter?.notifyDataSetChanged()
                binding.tvAddNewTag.text = null
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }


        // OK 버튼에 클릭 리스너 설정
        binding.btnOk.setOnClickListener {
            val selectedTags = multiChoiceList.filterValues { it }.keys.toList()
            tagSelectionListener?.onTagsSelected(selectedTags)
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun setTagSelectionListener(listener: TagSelectionListener) {
        tagSelectionListener = listener
    }

    companion object {
        fun newInstance(multiChoiceList: LinkedHashMap<String, Boolean>): TagListFragment {
            val fragment = TagListFragment()
            val args = Bundle()
            args.putSerializable("multiChoiceList", multiChoiceList)
            fragment.arguments = args
            return fragment
        }
    }
}
