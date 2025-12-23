package com.example.caps_project

import android.graphics.Bitmap

/**
 * 액티비티 간 Bitmap 전달을 위한 싱글톤 객체
 * - Intent 로 Bitmap 전달이 어려우므로 메모리에 잠시 저장해두기 위해 사용
 * - 사용 후 반드시 clear() 로 메모리 해제 권장
 */
object ImageSingleton {

    // 다른 액티비티에서 접근 가능한 Bitmap
    @Volatile
    var bitmap: Bitmap? = null

    // 메모리 해제 함수
    fun clear() {
        bitmap = null
    }
}
