package com.example.caps_project.models.responses

import com.google.gson.annotations.SerializedName

data class ResponseAddIngredient(
    @SerializedName("success") val success: Boolean,
    @SerializedName("ingredient_id") val ingredient_id: Int
)
