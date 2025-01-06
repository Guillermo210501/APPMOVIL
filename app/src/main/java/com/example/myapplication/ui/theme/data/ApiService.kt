package com.example.myapplication.ui.theme.data

import com.example.myapplication.ui.theme.screens.Api.ApiResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("NucleoDigital")
    suspend fun getServices(
        @Header("Authorization") token: String,
        @Body requestBody: Map<String, String>
    ): ApiResponse
}
