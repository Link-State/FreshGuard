package com.example.caps_project.models.responses

import com.google.gson.annotations.SerializedName

data class ResponseLoadRecipeList(
    @SerializedName("recipes") val recipes: List<RecipeSummaryList>
)
