package com.example.caps_project

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caps_project.databinding.ActivityMainBinding
import com.example.caps_project.models.requests.RequestLoadHistory
import com.example.caps_project.models.requests.RequestLoadIngredient
import com.example.caps_project.models.requests.RequestLogin
import com.example.caps_project.models.responses.HistoryList
import com.example.caps_project.models.responses.IngredientList
import com.example.caps_project.models.responses.ResponseLoadHistory
import com.example.caps_project.models.responses.ResponseLoadIngredient
import com.example.caps_project.models.responses.ResponseLogin
import com.example.caps_project.recyclerview.SummaryHistoryAdapter
import com.example.caps_project.recyclerview.SummaryItemAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.PrintStream
import java.io.PrintWriter
import kotlin.collections.get

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onResume() {
        super.onResume()

        // --- 1. View 연결 ---
        val checkButton = findViewById<Button>(R.id.btn_check)

        // "식재료 검사" 버튼 클릭 -> Z1Activity 실행
        checkButton.setOnClickListener {
            val intent = Intent(this, Z1Activity::class.java)
            startActivity(intent)
        }

        // 식재료 로드
        val userid = RequestLoadIngredient(Session.userId)
        var loadIngredientRetryCount = 0
        RetrofitBuilder.api.loadIngredient(userid).enqueue(object : Callback<ResponseLoadIngredient> {
            override fun onResponse(call: Call<ResponseLoadIngredient>, response: Response<ResponseLoadIngredient>) {
                if (response.isSuccessful) {
                    val ingredients = response.body()?.ingredients!!
                    if (ingredients.isEmpty()) {
                        val alert:List<IngredientList> = listOf(
                            IngredientList(-1, -1, -1, "", "", "식재료없음")
                        )
                        updateStoredItemList(alert)
                    } else {
                        val sorted_ingredients:List<IngredientList> = ingredients.sortedWith( compareBy(
                            {
                                if (it.expire.isEmpty()) {
                                    (9999 - 1970).toBigInteger() * 365.toBigInteger() * 24.toBigInteger() * 60.toBigInteger() * 60.toBigInteger() * 1000.toBigInteger() +
                                            12.toBigInteger() * 31.toBigInteger() * 24.toBigInteger() * 60.toBigInteger() * 60.toBigInteger() * 1000.toBigInteger() +
                                            31.toBigInteger() * 24.toBigInteger() * 60.toBigInteger() * 60.toBigInteger() * 1000.toBigInteger()
                                }
                                else {
                                    (it.expire.subSequence(0, 4).toString().toInt().toBigInteger() - 1970.toBigInteger()) * 365.toBigInteger() * 24.toBigInteger() * 60.toBigInteger() * 60.toBigInteger() * 1000.toBigInteger() +
                                            it.expire.subSequence(5, 7).toString().toInt().toString().toBigInteger() * 31.toBigInteger() * 24.toBigInteger() * 60.toBigInteger() * 60.toBigInteger() * 1000.toBigInteger() +
                                            it.expire.subSequence(8,10).toString().toInt().toString().toBigInteger() * 24.toBigInteger() * 60.toBigInteger() * 60.toBigInteger() * 1000.toBigInteger()
                                }
                            },
                            { it.level },
                            {
                                - ((it.created.subSequence(0, 4).toString().toInt().toBigInteger() - 1970.toBigInteger()) * 365.toBigInteger() * 24.toBigInteger() * 60.toBigInteger() * 60.toBigInteger() * 1000.toBigInteger()
                                        + it.created.subSequence(5, 7).toString().toInt().toBigInteger() * 31.toBigInteger() * 24.toBigInteger() * 60.toBigInteger() * 60.toBigInteger() * 1000.toBigInteger()
                                        + it.created.subSequence(8,10).toString().toInt().toBigInteger() * 24.toBigInteger() * 60.toBigInteger() * 60.toBigInteger() * 1000.toBigInteger())
                            },
                            { -(it.id) }
                        ))
                        updateStoredItemList(sorted_ingredients)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseLoadIngredient>, t: Throwable) {
                loadIngredientRetryCount += 1
                if (loadIngredientRetryCount <= RetrofitBuilder.retry) {
                    call.clone().enqueue(this)
                } else {

                }
            }
        })

        // 최근 검사 기록 로드
        val input = RequestLoadHistory(Session.userId)
        var loadHistoryRetryCount = 0
        RetrofitBuilder.api.loadHistory(input).enqueue(object : Callback<ResponseLoadHistory> {
            override fun onResponse(call: Call<ResponseLoadHistory>, response: Response<ResponseLoadHistory>) {
                if (response.isSuccessful) {
                    val history = response.body()?.history!!
                    if (history.isEmpty()) {
                        val alert:List<HistoryList> = listOf(
                            HistoryList(-1, -1, -1, "", "기록없음")
                        )
                        updateHistoryList(alert)
                    } else {
                        val sorted_history:List<HistoryList> = history.sortedWith( compareBy(
                            {
                                - ((it.dcm_date.subSequence(0, 4).toString().toInt() - 1970).toBigInteger() * 365.toBigInteger() * 24.toBigInteger() * 60.toBigInteger() * 60.toBigInteger() * 1000.toBigInteger()
                                        + it.dcm_date.subSequence(5, 7).toString().toInt().toBigInteger() * 31.toBigInteger() * 24.toBigInteger() * 60.toBigInteger() * 60.toBigInteger() * 1000.toBigInteger()
                                        + it.dcm_date.subSequence(8,10).toString().toInt().toBigInteger() * 24.toBigInteger() * 60.toBigInteger() * 60.toBigInteger() * 1000.toBigInteger())
                            },
                            { -(it.dcm_id) }
                        ))
                        updateHistoryList(sorted_history)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseLoadHistory>, t: Throwable) {
                loadHistoryRetryCount += 1
                if (loadHistoryRetryCount <= RetrofitBuilder.retry) {
                    call.clone().enqueue(this)
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dummy_item:List<IngredientList> = listOf(
            IngredientList(-1, -1, -1, "", "", "로드 중...")
        )

        val dummy_record:List<HistoryList> = listOf(
            HistoryList(-1, -1, -1, "", "로드 중...")
        )

        updateStoredItemList(dummy_item)
        updateHistoryList(dummy_record)

        // --- 1. View 연결 ---
        val storageSectionLayout = findViewById<ConstraintLayout>(R.id.layout_storage_section)

        // --- 2. 클릭 리스너 설정 ---
        // "보관중인 식재료" 영역 클릭 -> StorageActivity 이동
        storageSectionLayout.setOnClickListener {
            val intent = Intent(this, StorageActivity::class.java)
            startActivity(intent)
        }

        // --- 1. View 연결 ---
        val recentSectionLayout = findViewById<ConstraintLayout>(R.id.layout_recent_section)

        // "최근 검사 기록" 영역 클릭 -> RecentActivity 이동
        recentSectionLayout.setOnClickListener {
            val intent = Intent(this, RecentActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateStoredItemList(list: List<IngredientList>) {
        val adapter = SummaryItemAdapter(list)
        binding.storedItemSummaryListView.adapter = adapter
        binding.storedItemSummaryListView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        // 보관중인 식재료 텍스트뷰 그룹 (주황색 박스)
//        val storedItems = findViewById<RecyclerView>(R.id.storedItemSummaryListView).children.toList()


        // --- 3. B7 화면에서 전달받은 데이터 처리 ---

        // Intent에 "IS_REGISTERED"라는 키가 있는지 확인 (없으면 false)
//        val isRegistered = intent.getBooleanExtra("IS_REGISTERED", false)

        // Intent로 전달받은 식재료 이름 (예: "판별 식재료: 사과")
//        val rawFoodName = intent.getStringExtra("FOOD_NAME")

        // 데이터가 있다면 UI 업데이트 진행
//        if (rawFoodName != null) {
//
//            // 텍스트 다듬기 ("판별 식재료: 사과" -> "사과"만 남기기)
//            // 콜론(:)이 있으면 뒤에 있는 단어만 가져오고, 없으면 그대로 씁니다.
//            val displayFoodName = if (rawFoodName.contains(":")) {
//                rawFoodName.split(":")[1].trim()
//            } else {
//                rawFoodName
//            }
//
//            // (1) 보관중인 식재료 업데이트 (주황색 박스 첫 번째 칸)
//            // 체크박스에 체크가 되어 있을 때만 실행
//            if (isRegistered) {
////                storedItems[0]. = displayFoodName
//            }
//        }
    }

    private fun updateHistoryList(list: List<HistoryList>) {
        val adapter = SummaryHistoryAdapter(list)
        binding.historySummaryListView.adapter = adapter
        binding.historySummaryListView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        // 최근 검사 기록 텍스트뷰 그룹 (파란색 박스)
//        val recentItems = findViewById<RecyclerView>(R.id.historySummaryListView).children.toList()


        // --- 3. B7 화면에서 전달받은 데이터 처리 ---

        // Intent에 "IS_REGISTERED"라는 키가 있는지 확인 (없으면 false)
//        val isRegistered = intent.getBooleanExtra("IS_REGISTERED", false)

        // Intent로 전달받은 식재료 이름 (예: "판별 식재료: 사과")
//        val rawFoodName = intent.getStringExtra("FOOD_NAME")

        // 데이터가 있다면 UI 업데이트 진행
//        if (rawFoodName != null) {
//
//            // 텍스트 다듬기 ("판별 식재료: 사과" -> "사과"만 남기기)
//            // 콜론(:)이 있으면 뒤에 있는 단어만 가져오고, 없으면 그대로 씁니다.
//            val displayFoodName = if (rawFoodName.contains(":")) {
//                rawFoodName.split(":")[1].trim()
//            } else {
//                rawFoodName
//            }
//
//            // (2) 최근 검사 기록 업데이트 (파란색 박스 첫 번째 칸)
//            // 검사 결과는 등록 여부와 상관없이 무조건 최근 기록에 띄움
//            recentItems[0]. = displayFoodName
//        }
    }

    // (필요 시 사용하는 다이얼로그 함수)
    private fun showPhotoLoadDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_photo_load)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
}