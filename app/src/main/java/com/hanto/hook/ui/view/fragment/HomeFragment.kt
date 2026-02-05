package com.hanto.hook.ui.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanto.hook.R
import com.hanto.hook.data.model.Hook
import com.hanto.hook.data.model.UiState
import com.hanto.hook.databinding.FragmentHomeBinding
import com.hanto.hook.ui.adapter.HookAdapter
import com.hanto.hook.ui.view.activity.OnboardingActivity
import com.hanto.hook.ui.view.activity.WebViewActivity
import com.hanto.hook.util.BottomDialogHelper
import com.hanto.hook.viewmodel.HookViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), HookAdapter.OnItemClickListener {

    companion object {
        private const val TAG = "HomeFragment"
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    private val hookViewModel: HookViewModel by viewModels()
    private lateinit var adapter: HookAdapter

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
        Log.d(TAG, "onViewCreated()")
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupRecyclerView()
        setupSearchView()
        setupObservers()
        setupTutorialButton()
    }

    private fun setupAdapter() {
        adapter = HookAdapter(
            hooks = ArrayList(),
            onItemClickListener = this,
            onItemClick = { hook ->
                val intent = Intent(requireContext(), WebViewActivity::class.java).apply {
                    putExtra("HOOK_URL", hook.url)
                }
                startActivity(intent)
            }
        )
    }

    private fun setupRecyclerView() {
        binding.rvHome.adapter = adapter
        binding.rvHome.layoutManager = LinearLayoutManager(requireContext())

        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        binding.rvHome.addItemDecoration(dividerItemDecoration)
    }

    private fun setupSearchView() {
        val tvRecentHooks = binding.tvRecentHooks

        binding.svSearch.setOnSearchClickListener {
            tvRecentHooks.visibility = View.GONE

            val params = binding.svSearch.layoutParams
            params.width = LinearLayout.LayoutParams.MATCH_PARENT
            binding.svSearch.layoutParams = params
        }

        binding.svSearch.setOnCloseListener {
            tvRecentHooks.visibility = View.VISIBLE

            val params = binding.svSearch.layoutParams
            params.width = 50.dpToPx()
            binding.svSearch.layoutParams = params

            hookViewModel.setSearchQuery("")
            false
        }

        binding.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                hookViewModel.setSearchQuery(query ?: "")
                binding.svSearch.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                hookViewModel.setSearchQuery(newText ?: "")
                return true
            }
        })
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // 1. Hook 리스트 상태 관찰 (UiState: Loading, Success, Error)
                launch {
                    hookViewModel.hookUiState.collect { uiState ->
                        when (uiState) {
                            is UiState.Loading -> {
                                binding.sfLoading.startShimmer()
                                binding.sfLoading.visibility = View.VISIBLE
                            }

                            is UiState.Success -> {
                                // 로딩 종료
                                binding.sfLoading.stopShimmer()
                                binding.sfLoading.visibility = View.GONE

                                val hooksWithTags = uiState.data
                                val isNewDataAdded = hooksWithTags.size > adapter.itemCount

                                // 데이터가 비었을 때 안내 텍스트 처리
                                if (hooksWithTags.isEmpty()) {
                                    binding.txtAddHook.visibility = View.VISIBLE
                                } else {
                                    binding.txtAddHook.visibility = View.GONE
                                }

                                // 어댑터 업데이트
                                adapter.updateHooks(hooksWithTags) {
                                    // 새 데이터 추가 시 스크롤 맨 위로, 아니면 위치 유지 (기존 로직)
                                    val layoutManager =
                                        binding.rvHome.layoutManager as LinearLayoutManager
                                    val currentPosition =
                                        layoutManager.findFirstVisibleItemPosition()
                                    val offset =
                                        layoutManager.findViewByPosition(currentPosition)?.top ?: 0

                                    if (isNewDataAdded) {
                                        binding.rvHome.scrollToPosition(0)
                                    } else {
                                        layoutManager.scrollToPositionWithOffset(
                                            currentPosition,
                                            offset
                                        )
                                    }
                                }
                            }

                            is UiState.Error -> {
                                binding.sfLoading.stopShimmer()
                                binding.sfLoading.visibility = View.GONE
                                Log.e(TAG, "Error: ${uiState.message}")
                                Toast.makeText(
                                    requireContext(),
                                    uiState.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                // 2. 에러 메시지 관찰 (일회성 이벤트)
                launch {
                    hookViewModel.errorMessage.collect { errorMessage ->
                        errorMessage?.let {
                            Log.e(TAG, "Error: $it")
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                            hookViewModel.clearErrorMessage()
                        }
                    }
                }

                // 3. 글로벌 로딩 상태 관찰 (데이터 조작 시 발생)
                launch {
                    hookViewModel.isLoading.collect { isLoading ->
                        if (isLoading) {
                            binding.sfLoading.startShimmer()
                            binding.sfLoading.visibility = View.VISIBLE
                        } else {
                            // UiState.Success에서 이미 끄고 있지만, 안전장치로 유지
                            // 단, 리스트 로딩과 겹칠 수 있으므로 상황에 따라 조절 필요
                            // 여기서는 단순히 로딩바 제어만 수행
                            if (hookViewModel.hookUiState.value is UiState.Success) {
                                binding.sfLoading.stopShimmer()
                                binding.sfLoading.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupTutorialButton() {
        // 튜토리얼 다시보기
        val btSetting = view?.findViewById<ImageView>(R.id.btn_tut_again)
        btSetting?.setOnClickListener {
            val dialog = TwoButtonDialogFragment(
                title = getString(R.string.title_tut_again),
                content = getString(R.string.question_tut_again),
                positiveButtonText = getString(R.string.yes),
                negativeButtonText = getString(R.string.no),
                onPositiveClick = {
                    Intent(requireContext(), OnboardingActivity::class.java).also {
                        startActivity(it)
                    }
                }
            )
            dialog.show(parentFragmentManager, "TwoButtonDialog")
        }
    }

    override fun onResume() {
        Log.d(TAG, "onResume()")

        if (!binding.svSearch.isIconified) {
            binding.svSearch.setQuery("", false)
            binding.svSearch.isIconified = true
            binding.svSearch.clearFocus()

            val params = binding.svSearch.layoutParams
            params.width = 50.dpToPx()
            binding.svSearch.layoutParams = params
        }

        super.onResume()
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView()")
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")
        super.onDestroy()
    }

    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }

    override fun onClick(hook: Hook) {
        // 필요시 구현
    }

    override fun onOptionButtonClick(position: Int) {
        val selectedHook = adapter.getItem(position)
        BottomDialogHelper.showHookOptionsDialog(requireContext(), selectedHook, hookViewModel)
    }
}