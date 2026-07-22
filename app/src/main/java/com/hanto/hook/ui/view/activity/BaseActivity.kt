package com.hanto.hook.ui.view.activity

import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {

    /**
     * targetSdk 35+ 기기는 엣지 투 엣지가 강제되어, 화면 최상단/최하단 뷰가
     * 상태바·내비게이션바 뒤로 그려진다. 화면별 레이아웃마다 fitsSystemWindows를
     * 챙기지 않아도 되도록, setContentView 직후 호출되는 onContentChanged에서
     * 콘텐츠 루트에 상태바/내비게이션바 높이만큼 패딩을 주고 그 인셋을 소비해
     * 하위 뷰(툴바, 하단 버튼·탭바 등)가 같은 인셋을 중복으로 다시 패딩하지
     * 않게 한다. 기기마다 제스처 바/3버튼 내비게이션 바 높이가 달라 이 처리가
     * 없으면 화면 하단 요소가 기종별로 다르게 가려진다.
     */
    override fun onContentChanged() {
        super.onContentChanged()
        applySystemBarInsets(findViewById(android.R.id.content))
    }

    private fun applySystemBarInsets(root: View) {
        val initialPaddingTop = root.paddingTop
        val initialPaddingBottom = root.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val systemBarInset = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = initialPaddingTop + systemBarInset.top,
                bottom = initialPaddingBottom + systemBarInset.bottom
            )
            WindowInsetsCompat.Builder(insets)
                .setInsets(WindowInsetsCompat.Type.systemBars(), Insets.NONE)
                .build()
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            if (event.action == MotionEvent.ACTION_DOWN) {
                val view = currentFocus
                if (view != null && view is EditText) {
                    val outRect = Rect()
                    view.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        view.clearFocus()
                        val imm =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}