package com.hanto.hook.ui.view

import HookRepository
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.hanto.hook.database.AppDatabase
import com.hanto.hook.database.DatabaseModule
import com.hanto.hook.databinding.FragmentDeleteTagBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeleteTagFragment : DialogFragment() {

    val TAG = "DeleteTagFragment"

    interface OnTagDeletedListener {
        fun onTagDeleted()
    }

    private var listener: OnTagDeletedListener? = null

    fun setOnTagDeletedListener(listener: OnTagDeletedListener) {
        this.listener = listener
    }

    private var _binding: FragmentDeleteTagBinding? = null
    private val binding get() = _binding!!

    private lateinit var hookRepository: HookRepository
    private lateinit var appDatabase: AppDatabase


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeleteTagBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appDatabase = DatabaseModule.getDatabase()
        hookRepository = HookRepository(appDatabase)

        val selectedTagName = arguments?.getString("selectedTag")

        // 태그 삭제
        binding.btnDeleteTag.setOnClickListener {
            if (selectedTagName != null) {
                deleteTag(selectedTagName)
            }
        }

    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        _binding = null
    }


    private fun deleteTag(tagName: String) {
        Log.d(TAG, "deleteTag: $tagName")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                hookRepository.deleteTagByTagName(tagName)
                withContext(Dispatchers.Main) {
                    listener?.onTagDeleted()
                    dismiss()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "태그 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
