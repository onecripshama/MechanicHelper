package com.example.mechanichelper.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface PartsApi {
    @GET("products/search")
    suspend fun searchParts(@Query("q") query: String): PartsResponse

    @GET("products")
    suspend fun getAllParts(): PartsResponse
}

data class Part(
    val id: Int,
    val title: String,
    val description: String
)