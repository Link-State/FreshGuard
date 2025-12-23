package com.example.caps_project.models.responses

import com.google.gson.annotations.SerializedName

data class RecipeDetail(
    @SerializedName("sequence") val sequence: Int,
    @SerializedName("name") val name: String,
    @SerializedName("ingredients") val ingredients: String,
    @SerializedName("guides") val guides: List<RecipeGuideList>
)
