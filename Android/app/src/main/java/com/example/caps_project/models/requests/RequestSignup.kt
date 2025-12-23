package com.example.caps_project.models.requests

import com.google.gson.annotations.SerializedName

data class RequestSignup(
    @SerializedName("user_id") val user_id: String,
    @SerializedName("user_pwd") val user_pwd: String,
    @SerializedName("email") val email: String
)
