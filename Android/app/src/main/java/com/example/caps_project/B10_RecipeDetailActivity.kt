package com.example.caps_project

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caps_project.databinding.ActivityB10RecipeDetailBinding
import com.example.caps_project.models.requests.RequestAddRecipe
import com.example.caps_project.models.requests.RequestDeleteRecipe
import com.example.caps_project.models.requests.RequestLoadIngredient
import com.example.caps_project.models.requests.RequestLoadRecipeDetail
import com.example.caps_project.models.responses.RecipeDetail
import com.example.caps_project.models.responses.RecipeGuideList
import com.example.caps_project.models.responses.ResponseAddRecipe
import com.example.caps_project.models.responses.ResponseDeleteRecipe
import com.example.caps_project.models.responses.ResponseLoadIngredient
import com.example.caps_project.models.responses.ResponseLoadRecipeDetail
import com.example.caps_project.recyclerview.RecipeGuideAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class B10_RecipeDetailActivity : AppCompatActivity() {

    private lateinit var tvRecipeTitle: TextView
    private lateinit var tvSaveButton: TextView
    private lateinit var tvIngredients: TextView

    private var name: String = ""
    private var seq: Int = -1
    private var saved: Boolean = false
    private var isEditing: Boolean = false
    private var id: Int = -1


    lateinit var binding: ActivityB10RecipeDetailBinding

    // (30) 레시피 저장 상태를 관리하는 변수 (실제로는 ViewModel이나 DB에 저장해야 함)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityB10RecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        name = intent.getStringExtra("name")!!
        seq = intent.getIntExtra("seq", -1)
        saved = intent.getBooleanExtra("saved", false)
        id = intent.getIntExtra("id", -1)
        isEditing = false

        // UI 요소 연결
        tvRecipeTitle = findViewById(R.id.tv_recipe_title)
        tvSaveButton = findViewById(R.id.tv_save_button)
        tvIngredients = findViewById(R.id.tv_ingredients)

        // B8/B9에서 데이터 받기
//        val recipeName = intent.getStringExtra("RECIPE_NAME") ?: "레시피 상세"
//        tvRecipeTitle.text = recipeName

        // (가상) 레시피 이름에 따라 다른 재료 표시
//        if (recipeName.contains("두부")) {
//            tvIngredients.text = "두부 1모, 물, 소금 약간"
//        } else {
//            // 방금 XML에 입력한 시금치무침 재료와 일치시킴
//            tvIngredients.text = "시금치 50g, 볶은소금 0.5t, 참기름 0.5t, 통깨 0.5t"
//        }

        val title = "${seq.toString()}. $name"
        tvRecipeTitle.text = title

        updateSaveButtonUI(false)

        // (30) 저장 버튼 클릭 리스너
        tvSaveButton.setOnClickListener {
            if (isEditing) return@setOnClickListener
            isEditing = true

            if (saved) {
                // 레시피 삭제
                Toast.makeText(this, "삭제 중...", Toast.LENGTH_SHORT).show()
                val input = RequestDeleteRecipe(id)
                var retryCount = 0
                RetrofitBuilder.api.deleteRecipe(input).enqueue(object : Callback<ResponseDeleteRecipe> {
                    override fun onResponse(call: Call<ResponseDeleteRecipe>, response: Response<ResponseDeleteRecipe>) {
                        if (response.isSuccessful) {
                            if (response.body()?.success == true) {
                                saved = false
                                id = -1
                                updateSaveButtonUI()
                            } else {
                                Toast.makeText(this@B10_RecipeDetailActivity, "삭제에 실패했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@B10_RecipeDetailActivity, "삭제에 실패했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        }
                        isEditing = false
                    }

                    override fun onFailure(call: Call<ResponseDeleteRecipe>, t: Throwable) {
                        retryCount += 1
                        if (retryCount <= RetrofitBuilder.retry) {
                            call.clone().enqueue(this)
                        } else {
                            Toast.makeText(this@B10_RecipeDetailActivity, "삭제에 실패했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                            isEditing = false
                        }
                    }
                })
            } else {
                // 레시피 추가
                Toast.makeText(this, "저장 중...", Toast.LENGTH_SHORT).show()
                val input = RequestAddRecipe(Session.userId, seq, name)
                var retryCount = 0
                RetrofitBuilder.api.addRecipe(input).enqueue(object : Callback<ResponseAddRecipe> {
                    override fun onResponse(call: Call<ResponseAddRecipe>, response: Response<ResponseAddRecipe>) {
                        if (response.isSuccessful) {
                            if (response.body()?.success == true) {
                                saved = true
                                id = response.body()?.recipe_id!!
                                updateSaveButtonUI()
                            } else {
                                Toast.makeText(this@B10_RecipeDetailActivity, "저장에 실패했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@B10_RecipeDetailActivity, "저장에 실패했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        }
                        isEditing = false
                    }

                    override fun onFailure(call: Call<ResponseAddRecipe>, t: Throwable) {
                        retryCount += 1
                        if (retryCount <= RetrofitBuilder.retry) {
                            call.clone().enqueue(this)
                        } else {
                            Toast.makeText(this@B10_RecipeDetailActivity, "저장에 실패했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                            isEditing = false
                        }
                    }
                })
            }
        }


        // 조리법 상세 로드
        val input = RequestLoadRecipeDetail(Session.userId, name, seq)
        var retryCount = 0
        RetrofitBuilder.api.loadRecipeDetail(input).enqueue(object : Callback<ResponseLoadRecipeDetail> {
            override fun onResponse(call: Call<ResponseLoadRecipeDetail>, response: Response<ResponseLoadRecipeDetail>) {
                if (response.isSuccessful) {
                    val recipe = response.body()?.recipe!!
                    updateRecipe(recipe)
                } else {
                    val dummy:RecipeDetail = RecipeDetail(-1, "로드 실패, 다시 시도해주세요.", "", listOf())
                    updateRecipe(dummy)
                }
            }

            override fun onFailure(call: Call<ResponseLoadRecipeDetail>, t: Throwable) {
                retryCount += 1
                if (retryCount <= RetrofitBuilder.retry) {
                    call.clone().enqueue(this)
                } else {
                    val dummy:RecipeDetail = RecipeDetail(-1, "로드 실패, 다시 시도해주세요.", "", listOf())
                    updateRecipe(dummy)
                }
            }
        })
    }

    private fun updateRecipe(recipe: RecipeDetail) {
        val adapter = RecipeGuideAdapter(recipe.guides)
        binding.recipeGuideListView.adapter = adapter
        binding.recipeGuideListView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        tvIngredients.text = recipe.ingredients
    }

    /**
     * (30) 저장 버튼의 UI와 텍스트를 현재 상태(isRecipeSaved)에 맞게 업데이트
     */
    private fun updateSaveButtonUI(print:Boolean = true) {
        if (saved) {
            tvSaveButton.text = "삭제"
            // (선택) 삭제 버튼처럼 보이게 색상 변경
            tvSaveButton.setTextColor(Color.RED)
            if (print) {
                Toast.makeText(this, "레시피를 저장했습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            tvSaveButton.text = "저장"
            // (선택) 저장 버튼처럼 보이게 색상 변경
//             tvSaveButton.setTextColor(getColor(R.color.purple_500))
            if (print) {
                Toast.makeText(this, "레시피를 삭제했습니다.", Toast.LENGTH_SHORT).show() // '삭제'를 눌렀을 때 토스트
            }
        }
    }
}