package com.example.relax.models.network

import com.example.relax.models.endpoints.Flights
import com.example.relax.models.endpoints.FlightSearchResponse
import com.example.relax.models.endpoints.HotelDestinationResponse
import com.example.relax.models.endpoints.SearchHotelsResponse
import javax.inject.Inject

class HotelsRepository @Inject constructor(private val hotelsService: HotelsService) {

    suspend fun getHotelDestination(query: String?) : HotelDestinationResponse {
        return hotelsService.getHotelDestination(query)
    }

    suspend fun getHotels(
        destId: String?,
        arrivalDate: String,
        departureDate: String,
        adults: Int,
        children: String?,
    ): SearchHotelsResponse {
        return hotelsService.getHotels(
            destId = destId,
            arrivalDate = arrivalDate,
            departureDate = departureDate,
            adults = adults,
            children = children,
        )
    }

}