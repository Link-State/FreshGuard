package com.example.caps_project.models.requests

import com.google.gson.annotations.SerializedName

data class RequestDiscernmentIngredient(
    @SerializedName("user_uid") val user_uid: Int
)
