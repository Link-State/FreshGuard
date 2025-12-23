package com.example.caps_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.caps_project.models.requests.RequestLoadDiscernment
import com.example.caps_project.models.responses.DiscernmentResultList
import com.example.caps_project.models.responses.ResponseLoadDiscernment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class B6_LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b6_loading)
        val session_id = intent.getIntExtra("session_id", -1)

        if (session_id < 0) {
            Toast.makeText(this, "세션 번호 손실", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // AI 분석 시뮬레이션 (3초 대기)
//        lifecycleScope.launch {
//
//            delay(3000)
//        }

        val input = RequestLoadDiscernment(Session.userId, session_id)
        var retryCount = 0

        RetrofitBuilder.api.loadDiscernment(input).enqueue(object : Callback<ResponseLoadDiscernment> {
            override fun onResponse(call: Call<ResponseLoadDiscernment>, response: Response<ResponseLoadDiscernment>) {
                if (response.isSuccessful) {
                    if (response.body()?.success == true) {
                        // (시뮬레이션) AI가 분석 완료 후 결과 데이터 생성

                        Session.save.clear()

                        val results = ArrayList<IngredientResult>()
                        response.body()?.result?.forEach { item ->
                            results.add(
                                IngredientResult(
                                    item.discernment_id.toString(),
                                    Constant.Code2Name.get(item.ingre_num)!!,
                                    item.date.toString(),
                                    Constant.Level2Name.get(item.level)!!,
                                    Constant.Level2Emoji.get(item.level)!!,
                                    item.image.toString()
                                )
                            )
                        }

                        // B7 - 판별결과 화면으로 이동
                        val intent = Intent(this@B6_LoadingActivity, B7_ResultActivity::class.java).apply {
                            putParcelableArrayListExtra("RESULT_LIST", results)
                        }
                        startActivity(intent)
                        finish() // B6 화면은 스택에서 제거
                    }
                }
            }

            override fun onFailure(call: Call<ResponseLoadDiscernment>, t: Throwable) {
                retryCount += 1
                if (retryCount <= RetrofitBuilder.retry) {
                    call.clone().enqueue(this)
                }
            }
        })
    }
}