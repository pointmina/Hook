package com.hanto.hook.ui.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.util.query
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.hanto.hook.R
import com.hanto.hook.data.model.Hook
import com.hanto.hook.databinding.ActivityHookDetailBinding
import com.hanto.hook.databinding.ActivityWebviewBinding
import com.hanto.hook.databinding.FragmentHomeBinding
import com.hanto.hook.ui.adapter.HookAdapter
import com.hanto.hook.viewmodel.HookViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), HookAdapter.OnItemClickListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val hookViewModel: HookViewModel by viewModels()
    private lateinit var adapter: HookAdapter

    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HookAdapter(
            hooks = ArrayList(),
            hookViewModel = hookViewModel,
            lifecycleOwner = viewLifecycleOwner,
            onItemClickListener = this,
            onItemClick = { hook ->
                val intent = Intent(requireContext(), WebViewActivity::class.java).apply {
                    putExtra("HOOK_URL", hook.url)
                }
                startActivity(intent)
            }
        )

        binding.rvHome.adapter = adapter
        binding.rvHome.layoutManager = LinearLayoutManager(requireContext())

        // 리사이클러뷰에 구분선 추가
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        binding.rvHome.addItemDecoration(dividerItemDecoration)

        // liveDataHook을 관찰하여 데이터가 변경되면 어댑터에 업데이트
        hookViewModel.liveDataHook.observe(viewLifecycleOwner) { hooks: List<Hook> ->
            adapter.updateHooks(hooks)
        }

        // 설정 버튼 클릭 리스너
//        val btSetting = view.findViewById<ImageButton>(R.id.bt_setting)
//        btSetting.setOnClickListener {
//            findNavController().navigate(R.id.action_navigation_home_to_settingActivity)
//        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(hook: Hook) {
    }

    override fun onOptionButtonClick(position: Int) {
        val selectedHook = adapter.getItem(position)
        showBottomSheetDialog(selectedHook)
    }


    private fun showBottomSheetDialog(selectedItem: Hook) {
        val dialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottom_dialog_home, null)
        dialog.setContentView(view)
        dialog.setCancelable(true)

        // 수정하기 버튼 클릭 시
        val btnModifyHook: MaterialButton = view.findViewById(R.id.btn_modify_hook)
        btnModifyHook.setOnClickListener {
            val intent = Intent(requireContext(), HookDetailActivity::class.java)  // HookDetailActivity로 수정
            intent.putExtra("HOOK", selectedItem)
            startActivity(intent)
            dialog.dismiss()
        }

        // 삭제하기 버튼 클릭 시
        val btnDeleteHook: MaterialButton = view.findViewById(R.id.bt_delete_hook)
        btnDeleteHook.setOnClickListener {
            selectedItem.let {
                lifecycleScope.launch {
                    hookViewModel.deleteHookAndTags(selectedItem.hookId)
                }
            }
            dialog.dismiss()
        }

        // BottomSheet 표시
        dialog.show()
    }

}
