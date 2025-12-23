package com.example.caps_project.models.responses

import com.google.gson.annotations.SerializedName

data class IngredientList(
    @SerializedName("id") val id: Int,
    @SerializedName("ingredient_num") val ingre_num: Int,
    @SerializedName("level") val level: Int,
    @SerializedName("expire") val expire: String,
    @SerializedName("created") val created: String,
    @SerializedName("image") val image: String
)
