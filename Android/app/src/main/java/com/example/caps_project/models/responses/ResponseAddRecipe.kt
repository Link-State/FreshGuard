package com.example.caps_project.models.responses

import com.google.gson.annotations.SerializedName

data class ResponseAddRecipe(
    @SerializedName("success") val success: Boolean,
    @SerializedName("recipe_id") val recipe_id: Int
)
