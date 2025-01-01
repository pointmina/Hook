package com.hanto.hook.ui.view

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
                "Hello Food!",
                "The easiest way to order food from your favorite restaurant!",
                R.drawable.ic_hook_black
            ),
            OnboardingItem(
                "Fast Delivery",
                "Get your food delivered to your doorstep in no time!",
                R.drawable.ic_hook_purple
            ),
            OnboardingItem(
                "Enjoy Your Meal",
                "Relax and enjoy your delicious meal!",
                R.drawable.ic_hook_black
            ),
            OnboardingItem(
                "You're Ready!",
                "Click 'Let's Start' to begin your journey!",
                R.drawable.ic_hook_purple,
                isLastPage = true // 마지막 페이지 표시
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
                binding.buttonNext.visibility = if (isLastPage) View.GONE else View.VISIBLE
                binding.buttonSkip.visibility = if (isLastPage) View.GONE else View.VISIBLE
                binding.buttonBack.visibility = if (isLastPage) View.VISIBLE else View.GONE
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
