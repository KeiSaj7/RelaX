package com.example.relax.models.network

import com.example.relax.models.Flights
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("flights/searchDestination")
    suspend fun getDestination(@Query("query") query: String): Flights
}