package com.example.mechanichelper.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MechanicApi {

    @GET("cars")
    suspend fun getCars(): List<CarDto>

    @POST("cars")
    suspend fun createCar(@Body body: CreateCarRequest): CarDto

    @PUT("cars/{id}")
    suspend fun updateCar(@Path("id") id: String, @Body body: UpdateCarRequest): CarDto

    @DELETE("cars/{id}")
    suspend fun deleteCar(@Path("id") id: String): Response<Unit>

    @GET("reminders")
    suspend fun getReminders(): List<ReminderDto>

    @POST("reminders")
    suspend fun createReminder(@Body body: CreateReminderRequest): ReminderDto

    @POST("reminders/delete")
    suspend fun deleteReminder(@Body body: DeleteByIdRequest): Response<DeleteResponse>

    @GET("cars/{carId}/recommendations")
    suspend fun getRecommendations(@Path("carId") carId: String): List<RecommendationDto>

    @POST("cars/{carId}/recommendations")
    suspend fun createRecommendation(
        @Path("carId") carId: String,
        @Body body: CreateRecommendationRequest
    ): RecommendationDto

    @POST("cars/{carId}/recommendations/delete")
    suspend fun deleteRecommendation(
        @Path("carId") carId: String,
        @Body body: DeleteByIdRequest
    ): Response<DeleteResponse>
}

data class CarDto(
    val id: String,
    val name: String,
    val mileage: Int
)

data class CreateCarRequest(
    val name: String,
    val mileage: Int
)

data class UpdateCarRequest(
    val name: String,
    val mileage: Int
)

data class ReminderDto(
    val id: String,
    val text: String
)

data class CreateReminderRequest(
    val text: String
)

data class RecommendationDto(
    val id: String,
    val text: String
)

data class CreateRecommendationRequest(
    val text: String
)

data class DeleteByIdRequest(
    val id: String
)

data class DeleteResponse(
    val success: Boolean = true
)
