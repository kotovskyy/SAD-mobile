package com.example.sad.api.auth

import com.example.sad.BACKEND_ROOT_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call

data class SignupRequest(val username: String, val email: String, val password: String, val password2: String)
data class SignupResponse(val success: Boolean, val message: String)
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val success: Boolean, val message: String, val token: String?)

interface AuthApiService {
    @POST("users/login/")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("users/register/")
    fun register(@Body signupRequest: SignupRequest): Call<SignupResponse>
}

object AuthRetrofitInstance {
    val api: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BACKEND_ROOT_URL) // Replace with your actual API URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
}