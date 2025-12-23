package com.example.caps_project.models.responses

import com.google.gson.annotations.SerializedName

data class RecipeSummaryList(
    @SerializedName("id") val id: Int,
    @SerializedName("sequence") val sequence: Int,
    @SerializedName("name") val name: String,
    @SerializedName("create") val create: String
)
