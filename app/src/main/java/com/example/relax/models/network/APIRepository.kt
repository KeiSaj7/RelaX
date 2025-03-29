package com.example.relax.models.network

import com.example.relax.models.endpoints.searchFlightLocation.Flights
import com.example.relax.models.endpoints.searchFlights.FlightSearchResponse
import javax.inject.Inject

class APIRepository @Inject constructor(private val apiService: APIService) {

    suspend fun getDestination(query: String): Flights {
        return apiService.getDestination(query)
    }

    suspend fun getFlights(
        fromId: String,
        toId: String,
        departDate: String,
        returnDate: String?,
        adults: Int,
        children: String?,
        sort: String,
    ): FlightSearchResponse {
        return apiService.getFlights(
            fromId = fromId,
            toId = toId,
            departDate = departDate,
            returnDate = returnDate,
            adults = adults,
            children = children,
            sort = sort,
        )
    }
}