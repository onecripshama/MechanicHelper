package com.example.mechanichelper.data.api

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
    suspend fun deleteCar(@Path("id") id: String)

    @GET("reminders")
    suspend fun getReminders(): List<ReminderDto>

    @POST("reminders")
    suspend fun createReminder(@Body body: CreateReminderRequest): ReminderDto

    @DELETE("reminders")
    suspend fun deleteReminders(@Body body: DeleteRemindersRequest)

    @GET("cars/{carId}/recommendations")
    suspend fun getRecommendations(@Path("carId") carId: String): List<RecommendationDto>

    @POST("cars/{carId}/recommendations")
    suspend fun createRecommendation(
        @Path("carId") carId: String,
        @Body body: CreateRecommendationRequest
    ): RecommendationDto

    @DELETE("cars/{carId}/recommendations")
    suspend fun deleteRecommendations(
        @Path("carId") carId: String,
        @Body body: DeleteRecommendationsRequest
    )
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

data class DeleteRemindersRequest(
    val ids: List<String>
)

data class RecommendationDto(
    val id: String,
    val text: String
)

data class CreateRecommendationRequest(
    val text: String
)

data class DeleteRecommendationsRequest(
    val ids: List<String>
)
