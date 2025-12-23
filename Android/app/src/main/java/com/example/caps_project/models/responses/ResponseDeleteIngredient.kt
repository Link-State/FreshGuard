package com.example.caps_project.models.responses

import com.google.gson.annotations.SerializedName

data class ResponseDeleteIngredient(
    @SerializedName("success") val success: Boolean
)
