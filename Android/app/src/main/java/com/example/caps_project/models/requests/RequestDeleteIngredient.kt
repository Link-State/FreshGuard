package com.example.caps_project.models.requests

import com.google.gson.annotations.SerializedName

data class RequestDeleteIngredient(
    @SerializedName("ingredient_id") val ingredient_id: Int
)
