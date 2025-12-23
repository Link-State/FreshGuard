package com.example.caps_project.models.responses

import com.google.gson.annotations.SerializedName

data class ResponseLoadDiscernment(
    @SerializedName("success") val success: Boolean,
    @SerializedName("result") val result: List<DiscernmentResultList>
)
