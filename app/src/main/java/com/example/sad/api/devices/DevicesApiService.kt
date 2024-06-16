package com.example.sad.api.devices

import androidx.compose.ui.platform.LocalContext
import com.example.sad.BACKEND_ROOT_URL
import com.example.sad.HomeActivity.Device
import com.example.sad.HomeActivity.DeviceSetting
import com.example.sad.HomeActivity.Measurement
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
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

data class SettingUpdateRequest(val value: Float?)
data class SettingUpdateResponse(val success: Boolean, val message: String)

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

    @GET("measurements/")
    fun getDeviceMeasurements(@Query("device") device: Int): Call<List<Measurement>>

    @GET("settings/")
    fun getDeviceSettings(@Query("device") device: Int): Call<List<DeviceSetting>>

    @PATCH("settings/{setting_id}/")
    fun updateDeviceSetting(@Path("setting_id") settingId: Int, @Body settingUpdateRequest: SettingUpdateRequest): Call<SettingUpdateResponse>
}

object DevicesRetrofitInstance {
    fun createApi(token: String?): DevicesApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token))
            .build()

        return Retrofit.Builder()
            .baseUrl(BACKEND_ROOT_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DevicesApiService::class.java)
    }
}
