package com.example.caps_project

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ResultAdapter(
    fragmentActivity: FragmentActivity,
    private val results: List<IngredientResult>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = results.size

    override fun createFragment(position: Int): Fragment {
        // 각 포지션에 맞는 결과 데이터를 Fragment로 전달
        return ResultItemFragment.newInstance(results[position])
    }
}