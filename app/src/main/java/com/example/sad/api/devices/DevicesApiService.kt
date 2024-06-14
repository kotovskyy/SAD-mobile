package com.example.sad.api.devices

import androidx.compose.ui.platform.LocalContext
import com.example.sad.HomeActivity.Device
import com.example.sad.api.auth.AuthApiService
import com.example.sad.api.auth.SecureStorage
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class DevicesResponse(
    val success: Boolean,
    val devices: List<Device>,
    val message: String?
)

class AuthInterceptor(private val token: String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        // Add the token to the request header
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Token $token")
            .build()

        return chain.proceed(newRequest)
    }
}

interface DevicesApiService {
    @GET("devices/")
    fun getAllDevices(): Call<List<Device>>
}

object DevicesRetrofitInstance {
    fun createApi(token: String?): DevicesApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token))
            .build()

        return Retrofit.Builder()
            .baseUrl("http://192.168.1.102:8000")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DevicesApiService::class.java)
    }
}
