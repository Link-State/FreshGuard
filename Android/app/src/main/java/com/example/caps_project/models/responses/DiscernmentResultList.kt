package com.example.caps_project.models.responses

import com.google.gson.annotations.SerializedName

data class DiscernmentResultList(
    @SerializedName("discernment_id") val discernment_id: Int,
    @SerializedName("image") val image: String,
    @SerializedName("ingre_num") val ingre_num: Int,
    @SerializedName("date") val date: String,
    @SerializedName("level") val level: Int
)
