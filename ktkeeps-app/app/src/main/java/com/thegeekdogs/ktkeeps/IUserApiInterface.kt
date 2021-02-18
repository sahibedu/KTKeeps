package com.thegeekdogs.ktkeeps

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.POST

/**
 * @author Sahib Singh
 * @since 19/02/21
 **/
interface IUserApiInterface {

    @POST("users/create")
    suspend fun registerUser(
        @Field("password") password: String,
        @Field("displayName") displayName: String,
        @Field("email") email: String
    ): Response<BaseResponseModel<String>>

    @POST("users/login")
    suspend fun loginUser(
        @Field("password") password: String,
        @Field("email") email: String
    ): Response<BaseResponseModel<String>>
}