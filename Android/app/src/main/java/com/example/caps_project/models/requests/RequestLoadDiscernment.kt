package com.example.caps_project.models.requests

import com.google.gson.annotations.SerializedName

data class RequestLoadDiscernment(
    @SerializedName("user_uid") val user_uid: Int,
    @SerializedName("session_id") val session_id: Int
)
