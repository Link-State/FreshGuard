package com.example.caps_project.models.responses

import com.google.gson.annotations.SerializedName

data class RecommendRecipeList(
    @SerializedName("sequence") val sequence: Int,
    @SerializedName("name") val name: String,
    @SerializedName("saved") val saved: Boolean,
    @SerializedName("id") val id: Int
)
