package com.example.caps_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caps_project.databinding.ActivityStorageBinding
import com.example.caps_project.models.requests.RequestLoadIngredient
import com.example.caps_project.models.responses.IngredientList
import com.example.caps_project.models.responses.ResponseLoadIngredient
import com.example.caps_project.recyclerview.DetailItemAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StorageActivity : AppCompatActivity() {

    lateinit var binding: ActivityStorageBinding

    override fun onResume() {
        super.onResume()

        // 식재료 로드
        val userid = RequestLoadIngredient(Session.userId)
        var loadIngredientRetryCount = 0
        RetrofitBuilder.api.loadIngredient(userid).enqueue(object : Callback<ResponseLoadIngredient> {
            override fun onResponse(call: Call<ResponseLoadIngredient>, response: Response<ResponseLoadIngredient>) {
                if (response.isSuccessful) {
                    val ingredients = response.body()?.ingredients!!
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

            override fun onFailure(call: Call<ResponseLoadIngredient>, t: Throwable) {
                loadIngredientRetryCount += 1
                if (loadIngredientRetryCount <= RetrofitBuilder.retry) {
                    call.clone().enqueue(this)
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStorageBinding.inflate(layoutInflater)
        setContentView(binding.root) // B2 화면 XML
    }

    private fun updateStoredItemList(list: List<IngredientList>) {

        val adapter = DetailItemAdapter(list)
        binding.detailItemListView.adapter = adapter
        binding.detailItemListView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        // 버튼 연결
        val btnAdd = findViewById<Button>(R.id.btn_add)
        val btnMyRecipe = findViewById<Button>(R.id.btn_my_recipe)

        // 1. "식재료 추가" 버튼 클릭 -> DetailActivity (B3)로 이동
        // (데이터 없이 이동하므로 DetailActivity에서는 빈 칸으로 나옵니다)
        btnAdd.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java)
            // MODE : 1 = 추가, 2 = 수정
            intent.putExtra("MODE", 1)
            startActivity(intent)
        }

        // 2. "나의 레시피" 버튼 클릭 -> B8_RecipeListActivity로 이동
        btnMyRecipe.setOnClickListener {
            val intent = Intent(this, B8_RecipeListActivity::class.java)
            startActivity(intent)
        }

        // 화면의 아이템 색상 업데이트 (예시)
        updateItemColors()
    }

    private fun updateItemColors() {
//        val item1 = findViewById<TextView>(R.id.tv_days_left_item1)
//        val item2 = findViewById<TextView>(R.id.tv_days_left_item2)
//        val item3 = findViewById<TextView>(R.id.tv_days_left_item3)
//
//        // null 체크(?.)를 사용하여 안전하게 색상 변경
//        item1?.setTextColor(getColor(android.R.color.holo_green_dark))
//        item2?.setTextColor(getColor(android.R.color.holo_orange_dark))
//        item3?.setTextColor(getColor(android.R.color.holo_red_dark))
    }
}