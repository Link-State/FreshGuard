package com.example.caps_project.interfaces

import com.example.caps_project.models.requests.RequestAddRecipe
import com.example.caps_project.models.requests.RequestDeleteIngredient
import com.example.caps_project.models.requests.RequestDeleteRecipe
import com.example.caps_project.models.requests.RequestIdCheck
import com.example.caps_project.models.requests.RequestLoadDiscernment
import com.example.caps_project.models.requests.RequestLoadHistory
import com.example.caps_project.models.requests.RequestLoadIngredient
import com.example.caps_project.models.requests.RequestLoadRecipeDetail
import com.example.caps_project.models.requests.RequestLoadRecipeList
import com.example.caps_project.models.requests.RequestLogin
import com.example.caps_project.models.requests.RequestRecommendRecipe
import com.example.caps_project.models.requests.RequestSignup
import com.example.caps_project.models.responses.ResponseAddIngredient
import com.example.caps_project.models.responses.ResponseAddRecipe
import com.example.caps_project.models.responses.ResponseDeleteIngredient
import com.example.caps_project.models.responses.ResponseDeleteRecipe
import com.example.caps_project.models.responses.ResponseDiscernmentIngredient
import com.example.caps_project.models.responses.ResponseIdCheck
import com.example.caps_project.models.responses.ResponseLoadDiscernment
import com.example.caps_project.models.responses.ResponseLoadHistory
import com.example.caps_project.models.responses.ResponseLoadIngredient
import com.example.caps_project.models.responses.ResponseLoadRecipeDetail
import com.example.caps_project.models.responses.ResponseLoadRecipeList
import com.example.caps_project.models.responses.ResponseLogin
import com.example.caps_project.models.responses.ResponseModifyIngredient
import com.example.caps_project.models.responses.ResponseRecommendRecipe
import com.example.caps_project.models.responses.ResponseSignup
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface API {

    @POST("login")
    fun login(@Body body: RequestLogin): Call<ResponseLogin>

    @POST("signup")
    fun signup(@Body body: RequestSignup): Call<ResponseSignup>

    @POST("id-check")
    fun idCheck(@Body body: RequestIdCheck): Call<ResponseIdCheck>

    @Multipart
    @POST("discernment-ingredient")
    fun discernmentIngredient(
        @Part("user_uid") user_uid: RequestBody,
        @Part image: MultipartBody.Part?
    ): Call<ResponseDiscernmentIngredient>

    @POST("load-discernment")
    fun loadDiscernment(@Body body: RequestLoadDiscernment): Call<ResponseLoadDiscernment>

    @POST("load-ingredient")
    fun loadIngredient(@Body body: RequestLoadIngredient): Call<ResponseLoadIngredient>

    @Multipart
    @POST("add-ingredient")
    fun addIngredient(
        @Part("user_uid") user_uid: RequestBody,
        @Part("level") level: RequestBody,
        @Part("ingredient_num") ingredient_num: RequestBody,
        @Part("expire") expire: RequestBody?,
        @Part("discernment_id") discernment_id: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Call<ResponseAddIngredient>

    @Multipart
    @POST("modify-ingredient")
    fun modifyIngredient(
        @Part("ingredient_id") ingredient_id: RequestBody,
        @Part("level") level: RequestBody?,
        @Part("ingredient_num") ingredient_num: RequestBody?,
        @Part("expire") expire: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Call<ResponseModifyIngredient>

    @POST("delete-ingredient")
    fun deleteIngredient(@Body body: RequestDeleteIngredient): Call<ResponseDeleteIngredient>

    @POST("load-history")
    fun loadHistory(@Body body: RequestLoadHistory): Call<ResponseLoadHistory>

    @POST("load-recipe-list")
    fun loadRecipeList(@Body body: RequestLoadRecipeList): Call<ResponseLoadRecipeList>

    @POST("load-recipe-detail")
    fun loadRecipeDetail(@Body body: RequestLoadRecipeDetail): Call<ResponseLoadRecipeDetail>

    @POST("add-recipe")
    fun addRecipe(@Body body: RequestAddRecipe): Call<ResponseAddRecipe>

    @POST("delete-recipe")
    fun deleteRecipe(@Body body: RequestDeleteRecipe): Call<ResponseDeleteRecipe>

    @POST("recommend-recipe")
    fun recommendRecipe(@Body body: RequestRecommendRecipe): Call<ResponseRecommendRecipe>
}