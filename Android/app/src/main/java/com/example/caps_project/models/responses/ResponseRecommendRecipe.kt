package com.example.caps_project.models.responses

import com.google.gson.annotations.SerializedName

data class ResponseRecommendRecipe(
    @SerializedName("recipes") val recipes: List<RecommendRecipeList>
)
