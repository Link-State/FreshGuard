package com.example.caps_project.models.responses

import com.google.gson.annotations.SerializedName

data class ResponseDeleteRecipe(
    @SerializedName("success") val success: Boolean
)
