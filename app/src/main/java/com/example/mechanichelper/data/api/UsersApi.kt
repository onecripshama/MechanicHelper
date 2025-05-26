package com.example.mechanichelper.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface UsersApi {
    @GET("users/search")
    suspend fun searchUsers(@Query("q") query: String): UsersResponse

    @GET("users")
    suspend fun getAllUsers(): UsersResponse
}

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val phone: String
)