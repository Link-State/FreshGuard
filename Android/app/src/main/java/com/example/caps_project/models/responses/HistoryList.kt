package com.example.caps_project.models.responses

import com.google.gson.annotations.SerializedName

data class HistoryList(
    @SerializedName("dcm_id") val dcm_id: Int,
    @SerializedName("ingre_num") val ingre_num: Int,
    @SerializedName("level") val level: Int,
    @SerializedName("dcm_date") val dcm_date: String,
    @SerializedName("image") val image: String
)
