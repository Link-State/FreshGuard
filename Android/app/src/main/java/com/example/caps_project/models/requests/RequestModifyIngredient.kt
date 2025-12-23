package com.example.caps_project.models.requests

import com.google.gson.annotations.SerializedName

data class RequestModifyIngredient (
    @SerializedName("ingredient_id") val ingredient_id: Int,
    @SerializedName("ingredient_num") val ingredient_num: Int,
    @SerializedName("level") val level: Int,
    @SerializedName("expire") val expire: String
)