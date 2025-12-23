package com.example.caps_project // 1. 패키지 이름 확인

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.caps_project.R // 2. R 파일 import 확인

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // R.layout 사용

        val btnLogin: Button = findViewById(R.id.btnLogin) // R.id 사용
        val btnSignUp: Button = findViewById(R.id.btnSignUp) // R.id 사용

        // (1) 로그인 버튼 클릭
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // (2) 회원가입 버튼 클릭
        btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}