package com.example.sad

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call

data class SignupRequest(val username: String, val email: String, val password: String)
data class SignupResponse(val success: Boolean, val message: String)
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val success: Boolean, val message: String, val token: String?)

interface AuthApiService {
    @POST("login/")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("register/")
    fun register(@Body signupRequest: SignupRequest): Call<SignupResponse>
}

object RetrofitInstance {
    val api: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.1.102:8000") // Replace with your actual API URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
}