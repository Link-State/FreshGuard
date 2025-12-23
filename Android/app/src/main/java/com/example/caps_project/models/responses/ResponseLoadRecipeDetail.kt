package com.example.caps_project.models.responses

import com.google.gson.annotations.SerializedName

data class ResponseLoadRecipeDetail(
    @SerializedName("recipe") val recipe: RecipeDetail
)
