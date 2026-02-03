package com.hanto.hook.ui.view

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanto.hook.R
import com.hanto.hook.data.model.Hook
import com.hanto.hook.databinding.FragmentHomeBinding
import com.hanto.hook.ui.adapter.HookAdapter
import com.hanto.hook.util.BottomDialogHelper
import com.hanto.hook.util.SoundSearcher
import com.hanto.hook.viewmodel.HookViewModel
import dagger.hilt.android.AndroidEntryPoint

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

            false
        }

        binding.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterHooks(it) }
                binding.svSearch.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterHooks(it) }
                return true
            }
        })
    }

    private fun setupObservers() {
        hookViewModel.hooks.observe(viewLifecycleOwner) { hooksWithTags ->
            val layoutManager = binding.rvHome.layoutManager as LinearLayoutManager
            val currentPosition = layoutManager.findFirstVisibleItemPosition()
            val offset = layoutManager.findViewByPosition(currentPosition)?.top ?: 0

            val isNewDataAdded = hooksWithTags.size > adapter.itemCount

            if (hooksWithTags.isEmpty()) {
                binding.txtAddHook.visibility = View.VISIBLE
            } else {
                binding.txtAddHook.visibility = View.GONE
            }

            adapter.updateHooks(hooksWithTags) {
                val shimmerContainer = binding.sfLoading
                shimmerContainer.stopShimmer()
                shimmerContainer.visibility = View.GONE

                if (isNewDataAdded) {
                    binding.rvHome.scrollToPosition(0)
                } else {
                    layoutManager.scrollToPositionWithOffset(currentPosition, offset)
                }
            }
        }

        hookViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Log.e(TAG, "Error: $it")
                hookViewModel.clearErrorMessage()
            }
        }

        hookViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.sfLoading.startShimmer()
                binding.sfLoading.visibility = View.VISIBLE
            } else {
                binding.sfLoading.stopShimmer()
                binding.sfLoading.visibility = View.GONE
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

    private fun filterHooks(query: String) {
        val hooks = hookViewModel.hooks.value ?: emptyList() // List<HookWithTags>

        val filteredHooks = if (query.isBlank()) {
            hooks
        } else {
            hooks.filter { item ->
                SoundSearcher.matchString(item.hook.title, query) ||
                        (item.hook.description?.let { SoundSearcher.matchString(it, query) } ?: false)
            }
        }
        adapter.updateHooks(filteredHooks)
    }
}