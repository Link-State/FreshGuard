package com.example.caps_project // 1. 패키지 이름 확인

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.example.caps_project.R // 2. R 파일 import 확인
import com.example.caps_project.models.requests.RequestLogin
import com.example.caps_project.models.responses.ResponseLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // R.layout 사용

        val btnBack: ImageButton = findViewById(R.id.btnBack) // R.id 사용
        val btnLoginConfirm: Button = findViewById(R.id.btnLoginConfirm)
        val etId: TextInputEditText = findViewById(R.id.etId)
        val etPassword: TextInputEditText = findViewById(R.id.etPassword)
        val tvError: TextView = findViewById(R.id.tvError)

        // (3) 뒤로가기
        btnBack.setOnClickListener {
            finish() // 현재 액티비티 종료
        }

        // (5) 로그인 버튼
        btnLoginConfirm.setOnClickListener {
            val id = etId.text.toString()
            val password = etPassword.text.toString()

            val input = RequestLogin(id, password)
            var retryCount = 0
            RetrofitBuilder.api.login(input).enqueue(object : Callback<ResponseLogin> {
                override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                    if (response.isSuccessful) {
                        if (response.body()?.success == true) {
                            // 로그인 성공 -> B1 메인 화면으로 이동
                            tvError.visibility = View.GONE
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            // 로그인 성공 시 이전 화면(Splash)을 스택에서 제거
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            Session.userId = response.body()?.user_uid!!
                            Log.d("LinkState", "LoginActivity - userID = ${Session.userId}")
                            startActivity(intent)
                        } else {
                            // 로그인 실패 -> (4) 오류 알림
                            tvError.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                    retryCount += 1
                    if (retryCount <= RetrofitBuilder.retry) {
                        call.clone().enqueue(this)
                    }
                }
            })
        }
    }
}