package com.hanto.hook.util


import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: (T) -> Unit) {
    val singleObserver = object : Observer<T> {
        override fun onChanged(value: T) {
            value?.let {
                observer(it) // 람다 실행
                removeObserver(this) // 관찰 중지
            }
        }
    }
    observe(lifecycleOwner, singleObserver)
}
