package com.hanto.hook.ui.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.hanto.hook.R
import com.hanto.hook.data.model.OnboardingItem
import com.hanto.hook.databinding.ActivityOnboardingBinding
import com.hanto.hook.ui.adapter.OnboardingAdapter

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 온보딩 데이터 정의
        val onboardingItems = listOf(
            OnboardingItem(
                "",
                getString(R.string.des1),
                R.drawable.img_share
            ),
            OnboardingItem(
                "",
                getString(R.string.des2),
                R.drawable.img_tut11
            ),
            OnboardingItem(
                getString(R.string.title3),
                getString(R.string.des3),
                R.drawable.img_tut22
            ),
            OnboardingItem(
                getString(R.string.title4),
                getString(R.string.des4),
                R.drawable.img_tut33
            ),
            OnboardingItem(
                getString(R.string.title5),
                getString(R.string.des5),
                R.drawable.img_tut44,
                isLastPage = true
            )
        )


        // 어댑터 설정
        val onboardingAdapter = OnboardingAdapter(onboardingItems)
        binding.tutorialViewPager.adapter = onboardingAdapter

        // Dots Indicator
        binding.dotsIndicator.attachTo(binding.tutorialViewPager)

        // Skip & Next & Back 버튼 기능
        binding.buttonSkip.setOnClickListener { goToMainScreen() }

        // Next 버튼 동작
        binding.buttonNext.setOnClickListener {
            if (binding.tutorialViewPager.currentItem + 1 < onboardingAdapter.itemCount) {
                binding.tutorialViewPager.currentItem += 1
            }
        }

        // Back 버튼 동작
        binding.buttonBack.setOnClickListener {
            if (binding.tutorialViewPager.currentItem > 0) {
                binding.tutorialViewPager.currentItem -= 1
            }
        }


        // 페이지 변경 리스너
        binding.tutorialViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val isLastPage = position == onboardingAdapter.itemCount - 1
                val isFirstPage = position == 0
                binding.buttonNext.visibility = if (isLastPage) View.GONE else View.VISIBLE
                binding.buttonSkip.visibility = if (isLastPage) View.GONE else View.GONE
                binding.buttonBack.visibility =
                    if (isLastPage || isFirstPage) View.GONE else View.VISIBLE
                binding.buttonLetsStart.visibility = if (isLastPage) View.VISIBLE else View.GONE
            }
        })


        binding.buttonLetsStart.setOnClickListener { goToMainScreen() }
    }

    private fun goToMainScreen() {
        val sharedPref = getSharedPreferences("hook_prefs", MODE_PRIVATE)
        sharedPref.edit().putBoolean("isFirstLaunch", false).apply()

        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
