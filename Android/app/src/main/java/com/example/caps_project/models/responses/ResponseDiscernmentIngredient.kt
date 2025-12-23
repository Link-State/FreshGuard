package com.example.caps_project.models.responses

import com.google.gson.annotations.SerializedName

data class ResponseDiscernmentIngredient(
    @SerializedName("success") val success: Boolean,
    @SerializedName("session_id") val session_id: Int
)
