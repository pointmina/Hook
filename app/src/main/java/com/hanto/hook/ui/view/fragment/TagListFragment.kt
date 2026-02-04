package com.hanto.hook.ui.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanto.hook.R
import com.hanto.hook.data.TagSelectionListener
import com.hanto.hook.databinding.FragmentTagListBinding
import com.hanto.hook.ui.adapter.TagListAdapter
import com.hanto.hook.viewmodel.HookViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TagListFragment : DialogFragment() {

    companion object {
        fun newInstance(multiChoiceList: LinkedHashMap<String, Boolean>): TagListFragment {
            val fragment = TagListFragment()
            val args = Bundle()
            args.putSerializable("multiChoiceList", multiChoiceList)
            fragment.arguments = args
            return fragment
        }
    }

    private var tagSelectionListener: TagSelectionListener? = null
    private var _binding: FragmentTagListBinding? = null
    private val binding get() = _binding!!
    private lateinit var multiChoiceList: LinkedHashMap<String, Boolean>
    private lateinit var adapter: TagListAdapter

    private val hookViewModel: HookViewModel by viewModels()

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

        setupArguments()
        setupRecyclerView()
        setupViews()
        setupObservers()
    }

    private fun setupArguments() {
        arguments?.let {
            @Suppress("UNCHECKED_CAST")
            multiChoiceList =
                it.getSerializable("multiChoiceList") as LinkedHashMap<String, Boolean>
        }
    }

    private fun setupRecyclerView() {
        adapter = TagListAdapter(multiChoiceList)
        binding.lvTags.adapter = adapter
        binding.lvTags.layoutManager = LinearLayoutManager(requireContext())

        val dividerItemDecoration = DividerItemDecoration(
            requireContext(),
            LinearLayoutManager.VERTICAL
        )
        binding.lvTags.addItemDecoration(dividerItemDecoration)
    }

    private fun setupViews() {
        // 새 태그 추가 버튼
        binding.btnAddTag.setOnClickListener {
            val newTag = binding.tvAddNewTag.text.toString().trim()
            when {
                newTag.isEmpty() -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.plz_input_tag),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                multiChoiceList.containsKey(newTag) -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.exist_tag), Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    multiChoiceList[newTag] = true
                    adapter.notifyDataSetChanged()
                    binding.tvAddNewTag.text.clear()
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnOk.setOnClickListener {
            val selectedTags = multiChoiceList.filterValues { it }.keys.toList()
            tagSelectionListener?.onTagsSelected(selectedTags)
            dismiss()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // 1. 태그 목록 관찰
                launch {
                    hookViewModel.distinctTagNames.collect { tagNames ->
                        tagNames.forEach { tagName ->
                            if (!multiChoiceList.containsKey(tagName)) {
                                multiChoiceList[tagName] = false
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }
                }

                // 2. 에러 메시지 관찰
                launch {
                    hookViewModel.errorMessage.collect { errorMessage ->
                        errorMessage?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                            hookViewModel.clearErrorMessage()
                        }
                    }
                }
            }
        }
    }

    fun setTagSelectionListener(listener: TagSelectionListener) {
        tagSelectionListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}