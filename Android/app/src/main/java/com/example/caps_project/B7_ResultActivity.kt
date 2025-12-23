package com.example.caps_project

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.caps_project.databinding.ActivityB7ResultBinding
import com.example.caps_project.models.responses.ResponseAddIngredient
import com.example.caps_project.recyclerview.ViewPagerAdapter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class B7_ResultActivity : AppCompatActivity() {

    private lateinit var binding : ActivityB7ResultBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityB7ResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. 뷰 연결 (XML의 ID와 일치해야 함)
//        val cbRegister = findViewById<CheckBox>(R.id.cb_register)
        val btnComplete = findViewById<Button>(R.id.btn_complete)
//        val ivResultImage = findViewById<ImageView>(R.id.iv_result_image) // 이미지뷰 추가 연결

//        val tvResultName = findViewById<TextView>(R.id.tv_result_name)
//        val tvResultDate = findViewById<TextView>(R.id.tv_result_date)

        val result = intent.getParcelableArrayListExtra("RESULT_LIST", IngredientResult::class.java)!!
        if (result.isEmpty()) {
            result.add(
                IngredientResult(
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            )
        }

//        val view_pager = findViewById<ViewPager2>(R.id.result_flipper)
//        view_pager.adapter = ViewPagerAdapter(result)

        val adapter = ViewPagerAdapter(result)
        binding.resultFlipper.adapter = adapter

        // 2. [추가된 부분] 저장된 사진 불러와서 화면에 표시하기
        // ImageSingleton에 저장된 비트맵이 있다면 이미지뷰에 설정합니다.
//        if (ImageSingleton.bitmap != null) {
//            ivResultImage.setImageBitmap(ImageSingleton.bitmap)
//        }

        // 3. [완료] 버튼 클릭 이벤트
        btnComplete.setOnClickListener {
            val keyset = Session.save.keys.toList()
            keyset.forEach { key ->
                var retryCount = 0
                val userUID = Session.userId.toString() .toRequestBody("text/plain".toMediaTypeOrNull())
                val freshness = Constant.Name2Level.get(Session.save.get(key)!!.freshness)!!.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val ingre_num = Constant.Name2Code.get(Session.save.get(key)!!.name)!!.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val exDate = null
                val dcm_id = Session.save.get(key)!!.dcm_id.toRequestBody("text/plain".toMediaTypeOrNull())
                val imgFile = null
                RetrofitBuilder.api.addIngredient(
                    userUID,
                    freshness,
                    ingre_num,
                    exDate,
                    dcm_id,
                    imgFile
                ).enqueue(object : Callback<ResponseAddIngredient> {
                    override fun onResponse(call: Call<ResponseAddIngredient>, response: Response<ResponseAddIngredient>) {
                        if (response.isSuccessful) {
                            if (response.body()?.success == true) {
                                Log.d("ingredient saved", "success")
                            } else {
                                Log.d("ingredient saved", "fail")
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseAddIngredient>, t: Throwable) {
                        retryCount += 1
                        if (retryCount <= RetrofitBuilder.retry) {
                            call.clone().enqueue(this)
                        }
                    }
                })
            }

            // (1) 이동할 Intent 생성 (현재화면 -> 메인화면)
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            // (2) 체크박스가 체크되었는지 확인
//            val isRegistered = cbRegister.isChecked

            // (3) 데이터 담기 (PutExtra)
            // 메인 화면으로 '등록여부'와 '식재료 이름', '날짜' 등을 보냅니다.
//            intent.putExtra("IS_REGISTERED", isRegistered)
//            intent.putExtra("FOOD_NAME", tvResultName.text.toString()) // 예: "사과"
//            intent.putExtra("FOOD_DATE", tvResultDate.text.toString())

            // (4) 메인 화면 시작
            startActivity(intent)

            // (5) 현재 결과 화면은 종료 (뒤로가기 방지용)
            finish()
        }
    }
}