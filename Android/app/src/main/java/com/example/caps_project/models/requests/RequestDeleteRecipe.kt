package com.example.caps_project.models.requests

import com.google.gson.annotations.SerializedName

data class RequestDeleteRecipe(
    @SerializedName("recipe_id") val recipe_id: Int
)