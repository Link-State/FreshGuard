package com.example.caps_project.models.requests

import com.google.gson.annotations.SerializedName

data class RequestAddRecipe(
    @SerializedName("user_uid") val user_uid: Int,
    @SerializedName("sequence") val sequence: Int,
    @SerializedName("name") val name: String
)