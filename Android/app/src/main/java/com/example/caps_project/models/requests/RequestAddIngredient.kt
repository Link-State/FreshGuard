package com.example.caps_project.models.requests

import com.google.gson.annotations.SerializedName

data class RequestAddIngredient (
    @SerializedName("user_uid") val user_uid: Int,
    @SerializedName("level") val level: Int,
    @SerializedName("ingredient_num") val ingredient_num: Int,
    @SerializedName("expire") val expire: String?,
    @SerializedName("discernment_id") val discernment_id: Int
)