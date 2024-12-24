package com.hanto.hook.util

import android.util.Log

/**
 * 초성 검색 알고리즘
 */
object SoundSearcher {
    private const val HANGUL_BEGIN_UNICODE = 44032.toChar()
    private const val HANGUL_LAST_UNICODE = 55203.toChar()
    private const val HANGUL_BASE_UNIT = 588
    private val INITIAL_SOUNDS = charArrayOf(
        'ㄱ',
        'ㄲ',
        'ㄴ',
        'ㄷ',
        'ㄸ',
        'ㄹ',
        'ㅁ',
        'ㅂ',
        'ㅃ',
        'ㅅ',
        'ㅆ',
        'ㅇ',
        'ㅈ',
        'ㅉ',
        'ㅊ',
        'ㅋ',
        'ㅌ',
        'ㅍ',
        'ㅎ'
    )

    /**
     * 주어진 문자가 초기 자음인지 검사
     *
     * @param sound 검사할 자음
     * @return 초기 자음이면 true, 아니면 false
     */
    private fun isInitialSound(sound: Char): Boolean {
        for (initialSound in INITIAL_SOUNDS) {
            if (initialSound == sound) {
                return true
            }
        }
        return false
    }

    /**
     * 주어진 글자의 초성을 반환
     *
     * @param c 검사할 문자
     * @return 초성
     */
    private fun getInitialSound(c: Char): Char {
        val index = (c.code - HANGUL_BEGIN_UNICODE.code) / HANGUL_BASE_UNIT
        return INITIAL_SOUNDS[index]
    }

    /**
     * 주어진 문자가 한글인지 검사
     *
     * @param c 문자 하나
     * @return 한글이면 true, 아니면 false
     */
    private fun isHangul(c: Char): Boolean {
        return HANGUL_BEGIN_UNICODE <= c && c <= HANGUL_LAST_UNICODE
    }

    /**
     * 주어진 문자가 특수문자인지 검사
     *
     * @param c 문자
     * @return 특수 문자이면 true, 아니면 false
     */
    private fun isSpecialCharacter(c: Char): Boolean {
        return !isHangul(c) && !Character.isLetter(c) && !Character.isDigit(c)
    }

    private fun isMatchingChar(valueChar: Char, searchChar: Char): Boolean {
        if (isInitialSound(searchChar) && isHangul(valueChar)) {
            val match = getInitialSound(valueChar) == searchChar
            Log.d("SoundSearcher", "초성 매칭: valueChar=$valueChar, searchChar=$searchChar, result=$match")
            return match
        }
        val match = (isSpecialCharacter(searchChar) && valueChar == searchChar) || valueChar == searchChar
        Log.d("SoundSearcher", "일반 매칭: valueChar=$valueChar, searchChar=$searchChar, result=$match")
        return match
    }

    fun matchString(value: String, search: String): Boolean {
        if (search.isBlank()) return false

        val valueLength = value.length
        val searchLength = search.length

        if (searchLength > valueLength) return false

        for (i in 0..valueLength - searchLength) {
            var t = 0
            var isMatch = true
            while (t < searchLength) {
                if (!isMatchingChar(value[i + t], search[t])) {
                    isMatch = false
                    break
                }
                t++
            }
            if (isMatch) {
                Log.d("SoundSearcher", "matchString 성공: value=$value, search=$search")
                return true
            }
        }
        Log.d("SoundSearcher", "matchString 실패: value=$value, search=$search")
        return false
    }


}