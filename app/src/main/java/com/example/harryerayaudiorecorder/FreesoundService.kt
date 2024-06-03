package com.example.harryerayaudiorecorder

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface FreesoundService {
    @FormUrlEncoded
    @POST("oauth2/access_token/")
    fun exchangeCode(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String
    ): Call<TokenResponse>

    @GET("sounds/{sound_id}/download/")
    fun downloadSound(
        @Path("sound_id") soundId: String,
        @Header("Authorization") authToken: String
    ): Call<ResponseBody>


    @Multipart
    @POST("sounds/upload/")
    fun uploadSound(
        @Header("Authorization") authToken: String,
        @Part audiofile: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("tags") tags: RequestBody,
        @Part("description") description: RequestBody,
        @Part("license") license: RequestBody,
//        @Part("pack") pack: RequestBody,
//        @Part("geotag") geotag: RequestBody
    ): Call<ResponseBody>
}

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("scope") val scope: String,
    @SerializedName("refresh_token") val refreshToken: String
)
