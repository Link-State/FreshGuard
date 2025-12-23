package com.example.caps_project.models.responses

import com.google.gson.annotations.SerializedName

data class ResponseLogin(
    @SerializedName("success") val success: Boolean,
    @SerializedName("user_uid") val user_uid: Int
)
