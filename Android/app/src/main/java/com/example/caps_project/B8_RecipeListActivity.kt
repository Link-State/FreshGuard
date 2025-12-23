package com.example.caps_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caps_project.databinding.ActivityB8RecipeListBinding
import com.example.caps_project.models.requests.RequestLoadIngredient
import com.example.caps_project.models.requests.RequestLoadRecipeList
import com.example.caps_project.models.responses.RecipeSummaryList
import com.example.caps_project.models.responses.ResponseLoadIngredient
import com.example.caps_project.models.responses.ResponseLoadRecipeList
import com.example.caps_project.recyclerview.StoredRecipeAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class B8_RecipeListActivity : AppCompatActivity() {
    lateinit var binding: ActivityB8RecipeListBinding

    override fun onResume() {
        super.onResume()

        // (27) 추천 레시피 버튼
        val btnRecommendRecipe = findViewById<Button>(R.id.btn_recommend_recipe)

        // (28) 저장된 레시피 항목들 (예: 두부, 스위트콘, 두리안)
//        val btnRecipe1 = findViewById<TextView>(R.id.btn_recipe_1)
//        val btnRecipe2 = findViewById<TextView>(R.id.btn_recipe_2)
//        val btnRecipe3 = findViewById<TextView>(R.id.btn_recipe_3)

        // (27) 클릭 시 B9_RecommendedRecipeActivity로 이동
        btnRecommendRecipe.setOnClickListener {
            val intent = Intent(this, B9_RecommendedRecipeActivity::class.java)
            // B9에 어떤 식재료의 추천 레시피인지 전달 (예: "애호박")
//            intent.putExtra("INGREDIENT_NAME", "애호박")
            startActivity(intent)
        }

        // 보관중인 식재료 로드
        val userid = RequestLoadRecipeList(Session.userId)
        var retryCount = 0
        RetrofitBuilder.api.loadRecipeList(userid).enqueue(object : Callback<ResponseLoadRecipeList> {
            override fun onResponse(call: Call<ResponseLoadRecipeList>, response: Response<ResponseLoadRecipeList>) {
                if (response.isSuccessful) {
                    val ingredients = response.body()?.recipes!!
                    if (ingredients.isEmpty()) {
                        val alert:List<RecipeSummaryList> = listOf(
                            RecipeSummaryList(-1, -1, "저장된 레시피가 없습니다.", "")
                        )
                        updateStoredRecipeList(alert)
                    } else {
                        updateStoredRecipeList(ingredients)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseLoadRecipeList>, t: Throwable) {
                retryCount += 1
                if (retryCount <= RetrofitBuilder.retry) {
                    call.clone().enqueue(this)
                }
            }
        })

        // (28) 클릭 시 B10_RecipeDetailActivity로 이동하는 공통 리스너
//        val recipeClickListener = { view: android.view.View ->
//            val recipeName = (view as TextView).text.toString()
//            val intent = Intent(this, B10_RecipeDetailActivity::class.java)
//            intent.putExtra("RECIPE_NAME", recipeName)
//            startActivity(intent)
//        }

//        btnRecipe1.setOnClickListener(recipeClickListener)
//        btnRecipe2.setOnClickListener(recipeClickListener)
//        btnRecipe3.setOnClickListener(recipeClickListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityB8RecipeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dummy:List<RecipeSummaryList> = listOf(
            RecipeSummaryList(-1, -1, "로드 중...", "")
        )
        updateStoredRecipeList(dummy)
    }

    private fun updateStoredRecipeList(list: List<RecipeSummaryList>) {
        val adapter = StoredRecipeAdapter(list)
        binding.recipeListView.adapter = adapter
        binding.recipeListView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }
}

