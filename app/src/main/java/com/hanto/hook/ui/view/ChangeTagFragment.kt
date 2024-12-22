package com.hanto.hook.ui.view

import HookRepository
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.hanto.hook.data.TagUpdateListener
import com.hanto.hook.database.AppDatabase
import com.hanto.hook.database.DatabaseModule
import com.hanto.hook.databinding.FragmentChangeTagBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ChangeTagFragment : DialogFragment() {

    private val TAG = "ChangeTagFragment"

    private var _binding: FragmentChangeTagBinding? = null
    private val binding get() = _binding!!

    private var tagUpdateListener: TagUpdateListener? = null
    private lateinit var hookRepository: HookRepository
    private lateinit var appDatabase: AppDatabase

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

        appDatabase = DatabaseModule.getDatabase()
        hookRepository = HookRepository(appDatabase)

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
        CoroutineScope(Dispatchers.IO).launch {
            try {
                hookRepository.updateTagName(oldTagName, newTagName)
                withContext(Dispatchers.Main) {
                    tagUpdateListener?.onTagUpdated(newTagName)
                    dismiss()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "태그 이름 수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun setTagUpdateListener(listener: TagUpdateListener) {
        tagUpdateListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

