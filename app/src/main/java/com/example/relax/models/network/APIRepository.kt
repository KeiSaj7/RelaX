package com.example.relax.models.network

import com.example.relax.models.Flights
import javax.inject.Inject

class APIRepository @Inject constructor(private val apiService: APIService) {
    suspend fun getDestination(query: String): Flights = apiService.getDestination(query)
}