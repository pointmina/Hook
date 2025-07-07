package com.hanto.hook.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * 날짜 관련 유틸리티 클래스
 */
object DateUtils {

    private const val HOOK_ID_DATE_FORMAT = "yyyyMMddHHmmss"

    /**
     * 현재 시간을 기반으로 고유한 Hook ID를 생성
     * @return Hook ID로 사용할 문자열
     */
    fun generateHookId(): String {
        val dateFormat = SimpleDateFormat(HOOK_ID_DATE_FORMAT, Locale.getDefault())
        val currentTime = Calendar.getInstance().time
        return dateFormat.format(currentTime)
    }

    /**
     * 특정 포맷으로 현재 시간을 문자열로 반환
     * @param format 날짜 포맷 문자열
     * @return 포맷된 날짜 문자열
     */
    fun getCurrentTimeAsString(format: String): String {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        val currentTime = Calendar.getInstance().time
        return dateFormat.format(currentTime)
    }
}