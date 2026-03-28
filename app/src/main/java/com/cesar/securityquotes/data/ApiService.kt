package com.cesar.securityquotes.data

import com.cesar.securityquotes.model.DocumentRequest
import com.cesar.securityquotes.model.DocumentResponse
import com.cesar.securityquotes.model.LoginRequest
import com.cesar.securityquotes.model.LoginResponse
import com.cesar.securityquotes.model.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("api/auth/register")
    suspend fun register(
        @Part("username") username: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part image: MultipartBody.Part?
    ): RegisterResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/documents")
    suspend fun createDocument(
        @Header("Authorization") bearerToken: String,
        @Body request: DocumentRequest
    ): DocumentResponse
}
