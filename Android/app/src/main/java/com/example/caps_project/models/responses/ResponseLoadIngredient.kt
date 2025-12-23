package com.example.caps_project.models.responses

import com.google.gson.annotations.SerializedName

data class ResponseLoadIngredient(
    @SerializedName("ingredients") val ingredients: List<IngredientList>
)
