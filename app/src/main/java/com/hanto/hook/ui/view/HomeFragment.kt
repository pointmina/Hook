package com.hanto.hook.ui.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanto.hook.data.model.Hook
import com.hanto.hook.databinding.FragmentHomeBinding
import com.hanto.hook.ui.adapter.HookAdapter
import com.hanto.hook.util.BottomDialogHelper
import com.hanto.hook.viewmodel.HookViewModel

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
            val sortedHooks = hooks.sortedByDescending { it.id }
            adapter.updateHooks(sortedHooks)

            if (hooks.isNotEmpty()) {
                binding.rvHome.scrollToPosition(0)
            }
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
        BottomDialogHelper.showHookOptionsDialog(requireContext(), selectedHook, hookViewModel)
    }
}
