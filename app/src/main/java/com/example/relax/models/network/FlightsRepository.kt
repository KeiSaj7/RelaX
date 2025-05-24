package com.example.relax.models.network

import android.util.Log
import com.example.relax.models.endpoints.Flights
import com.example.relax.models.endpoints.FlightSearchResponse
import com.example.relax.models.endpoints.HotelDestinationResponse
import com.example.relax.models.endpoints.SearchHotelsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlightsRepository @Inject constructor(private val flightsService: FlightsService) {

    private val _flightsSearchResponse = MutableStateFlow<FlightSearchResponse?>(null)
    val flightsSearchResponse: StateFlow<FlightSearchResponse?> = _flightsSearchResponse.asStateFlow()

    fun clearResponse(){
        Log.d("RelaxLOG", "Flights data cleared.")
        _flightsSearchResponse.value = null
    }

    suspend fun getDestination(query: String): Flights {
        return flightsService.getDestination(query)
    }

    suspend fun getFlights(
        fromId: String,
        toId: String,
        departDate: String,
        returnDate: String?,
        adults: Int,
        children: String?,
        sort: String,
    ) {
        _flightsSearchResponse.value = flightsService.getFlights(
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