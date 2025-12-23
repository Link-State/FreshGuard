package com.example.caps_project

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.caps_project.models.requests.RequestIdCheck
import com.example.caps_project.models.requests.RequestLogin
import com.example.caps_project.models.requests.RequestSignup
import com.example.caps_project.models.responses.ResponseIdCheck
import com.example.caps_project.models.responses.ResponseLogin
import com.example.caps_project.models.responses.ResponseSignup
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    // 뷰 변수 선언
    private lateinit var etId: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etPasswordConfirm: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var btnSignUpConfirm: Button

    private lateinit var tvIdStatus: TextView
    private lateinit var tvPasswordStatus: TextView
    private lateinit var tvEmailStatus: TextView // [추가] 이메일 상태 텍스트뷰
    private lateinit var btnBack: ImageButton

    // 유효성 검사 상태 변수
    private var isIdValid = false
    private var isPwMatch = false
    private var isEmailValid = false // [추가] 이메일 유효성 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initViews()

        // (6) 뒤로가기 버튼 기능
        btnBack.setOnClickListener {
            finish()
        }

        // (7) 아이디 포커스 감지
        etId.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) checkIdAvailability()
        }

        // 아이디 수정 감지
        etId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInputForButtonEnable()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // (8) 비밀번호 일치 실시간 감지
        val passwordWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPasswordMatch()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        etPassword.addTextChangedListener(passwordWatcher)
        etPasswordConfirm.addTextChangedListener(passwordWatcher)

        // [수정] 이메일 입력 감지 -> 형식 검사 함수 호출
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkEmailFormat() // 이메일 형식을 실시간으로 검사
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // (9) 회원가입 버튼 클릭
        btnSignUpConfirm.setOnClickListener {
            val inputId = etId.text.toString().trim()
            val pw = etPassword.text.toString().trim()
            val email = etEmail.text.toString().trim()

            val input = RequestSignup(inputId, pw, email)
            var retryCount = 0

            RetrofitBuilder.api.signup(input).enqueue(object : Callback<ResponseSignup> {
                override fun onResponse(call: Call<ResponseSignup>, response: Response<ResponseSignup>) {
                    if (response.isSuccessful) {
                        if (response.body()?.success == true) {
                            Toast.makeText(this@SignUpActivity, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@SignUpActivity, "회원가입 실패, 다시 시도해주세요", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseSignup>, t: Throwable) {
                    retryCount += 1
                    if (retryCount <= RetrofitBuilder.retry) {
                        call.clone().enqueue(this)
                    } else {
                        Toast.makeText(this@SignUpActivity, "회원가입 실패, 다시 시도해주세요", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    private fun initViews() {
        etId = findViewById(R.id.etId)
        etPassword = findViewById(R.id.etPassword)
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm)
        etEmail = findViewById(R.id.etEmail)

        btnSignUpConfirm = findViewById(R.id.btnSignUpConfirm)
        tvIdStatus = findViewById(R.id.tvIdStatus)
        tvPasswordStatus = findViewById(R.id.tvPasswordStatus)
        tvEmailStatus = findViewById(R.id.tvEmailStatus) // [추가] 뷰 연결
        btnBack = findViewById(R.id.btnBack)
    }

    // 아이디 검사
    private fun checkIdAvailability() {
        val inputId = etId.text.toString().trim()

        val input = RequestIdCheck(inputId)
        var retryCount = 0;

        RetrofitBuilder.api.idCheck(input).enqueue(object : Callback<ResponseIdCheck> {
            override fun onResponse(call: Call<ResponseIdCheck>, response: Response<ResponseIdCheck>) {
                var isAlreadyExist = true

                if (response.isSuccessful) {
                    isAlreadyExist = response.body()?.isExist == true
                }

                if (inputId.length >= 4 && !isAlreadyExist) {
                    isIdValid = true
                    tvIdStatus.text = "사용할 수 있는 아이디입니다."
                    tvIdStatus.setTextColor(Color.parseColor("#4CAF50"))
                } else {
                    isIdValid = false
                    tvIdStatus.text = "사용할 수 없는 아이디입니다."
                    tvIdStatus.setTextColor(Color.parseColor("#F44336"))
                }

                if (inputId.isNotEmpty()) {
                    tvIdStatus.visibility = View.VISIBLE
                } else {
                    tvIdStatus.visibility = View.GONE
                    isIdValid = false
                }
                checkInputForButtonEnable()
            }

            override fun onFailure(call: Call<ResponseIdCheck>, t: Throwable) {
                retryCount += 1
                if (retryCount <= RetrofitBuilder.retry) {
                    call.clone().enqueue(this)
                }
            }
        })
    }

    // 비밀번호 일치 검사
    private fun checkPasswordMatch() {
        val pw = etPassword.text.toString().trim()
        val pwConfirm = etPasswordConfirm.text.toString().trim()

        if (pw.isEmpty() || pwConfirm.isEmpty()) {
            tvPasswordStatus.visibility = View.GONE
            isPwMatch = false
        } else if (pw == pwConfirm) {
            isPwMatch = true
            tvPasswordStatus.text = "비밀번호가 일치합니다."
            tvPasswordStatus.setTextColor(Color.parseColor("#4CAF50"))
            tvPasswordStatus.visibility = View.VISIBLE
        } else {
            isPwMatch = false
            tvPasswordStatus.text = "비밀번호가 일치하지 않습니다."
            tvPasswordStatus.setTextColor(Color.parseColor("#F44336"))
            tvPasswordStatus.visibility = View.VISIBLE
        }
        checkInputForButtonEnable()
    }

    // [추가] 이메일 형식 검사 함수
    private fun checkEmailFormat() {
        val email = etEmail.text.toString().trim()

        // 안드로이드 기본 패턴 활용하여 이메일 형식 검사
        val pattern = Patterns.EMAIL_ADDRESS

        if (email.isEmpty()) {
            tvEmailStatus.visibility = View.GONE
            isEmailValid = false
        } else if (!pattern.matcher(email).matches()) {
            // 형식이 틀릴 때
            isEmailValid = false
            tvEmailStatus.text = "올바른 이메일 형식이 아닙니다"
            tvEmailStatus.setTextColor(Color.parseColor("#F44336")) // 빨간색
            tvEmailStatus.visibility = View.VISIBLE
        } else {
            // 형식이 맞을 때 (문구 숨김 또는 성공 메시지)
            isEmailValid = true
            tvEmailStatus.visibility = View.GONE
            // 만약 "사용 가능한 이메일입니다"라고 띄우고 싶으면 여기서 처리
        }
        checkInputForButtonEnable()
    }

    // 버튼 활성화 체크
    private fun checkInputForButtonEnable() {
        // [수정] 아이디, 비번, 그리고 이메일 형식이 모두 유효해야 버튼 활성화
        val isEnable = isIdValid && isPwMatch && isEmailValid

        if (isEnable) {
            btnSignUpConfirm.isEnabled = true
            btnSignUpConfirm.background.setTint(Color.parseColor("#5cb85c"))
        } else {
            btnSignUpConfirm.isEnabled = false
            btnSignUpConfirm.background.setTint(Color.parseColor("#AAAAAA"))
        }
    }
}