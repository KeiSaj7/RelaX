package com.example.relax.models.network

import com.example.relax.models.endpoints.Flights
import com.example.relax.models.endpoints.FlightSearchResponse
import com.example.relax.models.endpoints.HotelDestinationResponse
import com.example.relax.models.endpoints.SearchHotelsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AttractionsService {
    @GET("attraction/searchLocation")
    suspend fun getAttractionLocation(
        @Query("query") query: String,
        @Query("languagecode") languagecode: String = "pl"
    ): Flights

    @GET("attraction/searchAttractions")
    suspend fun getAttractions(
        @Query("id") toId: String,
    ): FlightSearchResponse

}