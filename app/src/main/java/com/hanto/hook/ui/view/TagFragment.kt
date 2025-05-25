package com.hanto.hook.ui.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hanto.hook.R
import com.hanto.hook.databinding.FragmentTagBinding
import com.hanto.hook.ui.adapter.DragManageAdapterCallback
import com.hanto.hook.ui.adapter.TagAdapter
import com.hanto.hook.viewmodel.HookViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TagFragment : Fragment() {

    companion object {
        private const val TAG = "TagFragment"
    }

    private var _binding: FragmentTagBinding? = null
    private val binding get() = _binding!!
    private lateinit var tagAdapter: TagAdapter

    private val hookViewModel: HookViewModel by viewModels()

    private val addTagDialog by lazy {
        Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.activity_add_tag)

            val tvAddTagName = findViewById<EditText>(R.id.tv_add_tag_name)
            val btnAddTagName = findViewById<Button>(R.id.btn_add_tag_name)

            btnAddTagName.setOnClickListener {
                val name = tvAddTagName.text.toString().trim()
                if (name.isNotEmpty()) {
                    clearEditText(tvAddTagName)
                    this.dismiss()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.plz_input_tag), Toast.LENGTH_SHORT).show()
                }
            }

            val layoutParams = WindowManager.LayoutParams().apply {
                copyFrom(window?.attributes)
                width = WindowManager.LayoutParams.WRAP_CONTENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                gravity = Gravity.CENTER
            }
            window?.attributes = layoutParams
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTagBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupRecyclerView()
        setupObservers()
    }

    private fun setupViews() {
        val btAddTag: ImageButton = view?.findViewById(R.id.btAddTag) ?: return
        btAddTag.setOnClickListener {
            addTagDialog.show()
        }
    }

    private fun setupRecyclerView() {
        val flexboxLayoutManager = FlexboxLayoutManager(requireContext()).apply {
            justifyContent = JustifyContent.SPACE_EVENLY
            flexDirection = FlexDirection.ROW
        }
        binding.rvTagViewTagContainer.layoutManager = flexboxLayoutManager

        tagAdapter = TagAdapter(object : TagAdapter.OnItemClickListener {
            override fun onClick(tagName: String) {
                val intent = Intent(requireContext(), SelectedTagActivity::class.java).apply {
                    putExtra("selectedTagName", tagName)
                }
                startActivity(intent)
            }
        }).apply {
            recyclerView = binding.rvTagViewTagContainer
        }

        binding.rvTagViewTagContainer.adapter = tagAdapter

        val itemTouchHelper = ItemTouchHelper(DragManageAdapterCallback(tagAdapter))
        itemTouchHelper.attachToRecyclerView(binding.rvTagViewTagContainer)
    }

    private fun setupObservers() {
        hookViewModel.distinctTagNames.observe(viewLifecycleOwner) { tagNames ->
            Log.d(TAG, "distinctTagNames $tagNames")
            tagAdapter.submitList(tagNames)
        }

        // 에러 메시지 관찰
        hookViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                hookViewModel.clearErrorMessage()
            }
        }
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        _binding = null
    }

    private fun clearEditText(editText: EditText) {
        editText.text.clear()
    }
}