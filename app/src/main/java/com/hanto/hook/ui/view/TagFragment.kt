package com.hanto.hook.ui.view

import android.app.Dialog
import android.os.Bundle
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
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hanto.hook.R
import com.hanto.hook.databinding.FragmentTagBinding
import com.hanto.hook.ui.adapter.TagAdapter

class TagFragment : Fragment() {
    private var _binding: FragmentTagBinding? = null
    private val binding get() = _binding!!
    private lateinit var tagAdapter: TagAdapter

    private val dialog by lazy {
        Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.activity_add_tag)

            val tvChangeTagName = findViewById<EditText>(R.id.tv_change_tag_name)
            val btnChangeTagName = findViewById<Button>(R.id.btn_change_tag_name)

            btnChangeTagName.setOnClickListener {
                val name = tvChangeTagName.text.toString()
                if (name.isNotEmpty()) {
                    clearEditText(tvChangeTagName)
                    this.dismiss()
                } else {
                    Toast.makeText(requireContext(), "태그 이름을 입력하세요.", Toast.LENGTH_SHORT).show()
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

        val btAddTag: ImageButton = view.findViewById(R.id.btAddTag)
        btAddTag.setOnClickListener {
            dialog.show()
        }


        val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
            justifyContent = JustifyContent.SPACE_EVENLY
            flexDirection = FlexDirection.ROW
        }

    }


    private fun clearEditText(editText: EditText) {
        editText.text.clear()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}