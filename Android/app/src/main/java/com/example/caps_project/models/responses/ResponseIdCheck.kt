package com.example.caps_project.models.responses

import com.google.gson.annotations.SerializedName

data class ResponseIdCheck(
    @SerializedName("isExist") val isExist: Boolean
)
