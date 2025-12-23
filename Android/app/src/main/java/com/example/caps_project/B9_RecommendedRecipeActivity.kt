package com.example.caps_project

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.example.caps_project.databinding.ActivityB9RecommendedRecipeBinding
import com.example.caps_project.models.requests.RequestLoadRecipeList
import com.example.caps_project.models.requests.RequestRecommendRecipe
import com.example.caps_project.models.responses.RecommendRecipeList
import com.example.caps_project.models.responses.ResponseLoadRecipeList
import com.example.caps_project.models.responses.ResponseRecommendRecipe
import com.example.caps_project.recyclerview.RecommendRecipeAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class B9_RecommendedRecipeActivity : AppCompatActivity() {

    lateinit var binding: ActivityB9RecommendedRecipeBinding

    override fun onResume() {
        super.onResume()

        // 추천 조리법 로드
        val input = RequestRecommendRecipe(Session.userId)
        var retryCount = 0
        RetrofitBuilder.api.recommendRecipe(input).enqueue(object : Callback<ResponseRecommendRecipe> {
            override fun onResponse(call: Call<ResponseRecommendRecipe>, response: Response<ResponseRecommendRecipe>) {
                if (response.isSuccessful) {
                    val recipes = response.body()?.recipes!!
                    if (recipes.isEmpty()) {
                        val alert: List<RecommendRecipeList> = listOf(
                            RecommendRecipeList(-1, "먼저 식재료를 등록하세요.", false, -1)
                        )
                        updateRecommendRecipeList(alert)
                    } else {
                        updateRecommendRecipeList(recipes)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseRecommendRecipe>, t: Throwable) {
                retryCount += 1
                if (retryCount <= RetrofitBuilder.retry) {
                    call.clone().enqueue(this)
                }
                else {
                    val alert: List<RecommendRecipeList> = listOf(
                        RecommendRecipeList(-1, "로드 실패, 다시 시도해주세요.", false, -1)
                    )
                    updateRecommendRecipeList(alert)
                }
            }
        })


        // (29) 레시피 클릭 리스너
//        val listener = { view: android.view.View ->
//            val recipeName = (view as TextView).text.toString()
//            val intent = Intent(this, B10_RecipeDetailActivity::class.java).apply {
//                // (31)번 요구사항: "레시피" 탭이 선택되도록 "VIEW_MODE" 전달
//                putExtra("VIEW_MODE", "RECIPE")
//                putExtra("RECIPE_NAME", recipeName)
//            }
//            startActivity(intent)
//        }

//        tvRecipe1.setOnClickListener(listener)
//        tvRecipe2.setOnClickListener(listener)
//        tvRecipe3.setOnClickListener(listener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityB9RecommendedRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val tvRecipe1 = findViewById<TextView>(R.id.tv_recipe_1)
//        val tvRecipe2 = findViewById<TextView>(R.id.tv_recipe_2)
//        val tvRecipe3 = findViewById<TextView>(R.id.tv_recipe_3)

        val dummy:List<RecommendRecipeList> = listOf(
            RecommendRecipeList(-1, "로드 중...", false, -1)
        )
        updateRecommendRecipeList(dummy)

    }

    private fun updateRecommendRecipeList(list: List<RecommendRecipeList>) {
        val adapter = RecommendRecipeAdapter(list)
        binding.recommendRecipeListView.adapter = adapter
        binding.recommendRecipeListView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }
}