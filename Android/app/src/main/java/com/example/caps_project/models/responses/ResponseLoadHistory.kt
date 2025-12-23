package com.example.caps_project.models.responses

import com.google.gson.annotations.SerializedName

data class ResponseLoadHistory(
    @SerializedName("history") val history: List<HistoryList>
)
