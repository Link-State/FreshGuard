package com.example.caps_project.models.requests

import com.google.gson.annotations.SerializedName

data class RequestLoadRecipeDetail(
    @SerializedName("user_uid") val user_uid: Int,
    @SerializedName("name") val name: String,
    @SerializedName("sequence") val sequence: Int
)
