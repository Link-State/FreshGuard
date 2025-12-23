package com.example.caps_project.models.requests

import com.google.gson.annotations.SerializedName

data class RequestIdCheck(
    @SerializedName("user_id") val user_id: String
)
