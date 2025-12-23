package com.example.caps_project

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class ResultItemFragment : Fragment() {

    // Activity와 ViewModel 공유
    private val sharedViewModel: ResultViewModel by activityViewModels()
    private lateinit var result: IngredientResult

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // newInstance로부터 데이터 받기
        arguments?.let {
            result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable("RESULT_DATA", IngredientResult::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.getParcelable("RESULT_DATA")
            } ?: throw IllegalStateException("Result data is missing")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_result_item, container, false)

        val cbRegister = view.findViewById<CheckBox>(R.id.cb_register)
        val ivPhoto = view.findViewById<ImageView>(R.id.iv_photo)
        val tvName = view.findViewById<TextView>(R.id.tv_ingredient_name)
        val tvDate = view.findViewById<TextView>(R.id.tv_date)
        val tvFreshness = view.findViewById<TextView>(R.id.tv_freshness)

        // (25) 데이터 바인딩
        tvName.text = "판별 식재료: ${result.name}"
        tvDate.text = "판별 날짜: ${result.date}"
        tvFreshness.text = "판별 신선도: ${result.freshnessEmoji} - ${result.freshness}"
        // ivPhoto.load(...) // Glide/Picasso 등으로 이미지 로드

        // (23) 체크박스 로직
        // ViewModel의 현재 상태를 체크박스에 반영
        cbRegister.isChecked = sharedViewModel.itemsToRegister.value?.get(result.name) ?: false

        // 체크박스 클릭 시 ViewModel 업데이트
        cbRegister.setOnCheckedChangeListener { _, isChecked ->
            sharedViewModel.updateRegistration(result.name, isChecked)
        }

        return view
    }

    companion object {
        fun newInstance(result: IngredientResult) =
            ResultItemFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("RESULT_DATA", result)
                }
            }
    }
}