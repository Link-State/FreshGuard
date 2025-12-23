package com.example.caps_project.models.responses

import com.google.gson.annotations.SerializedName

data class RecipeGuideList(
    @SerializedName("text") val text: String,
    @SerializedName("image") val image: String
)
