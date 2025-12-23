package com.example.caps_project

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class IngredientResult(
    val dcm_id: String,
    val name: String,
    val date: String,
    val freshness: String,
    val freshnessEmoji: String, // (예: 🍎 - 낮음) 에서 이모지 부분
    val image: String
) : Parcelable