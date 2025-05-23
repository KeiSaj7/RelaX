package com.example.relax.models.network

import com.example.relax.models.endpoints.Flights
import com.example.relax.models.endpoints.FlightSearchResponse
import com.example.relax.models.endpoints.HotelDestinationResponse
import com.example.relax.models.endpoints.SearchHotelsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FlightsService {
    @GET("flights/searchDestination")
    suspend fun getDestination(
        @Query("query") query: String,
        @Query("languagecode") languagecode: String = "pl"
    ): Flights

    @GET("flights/searchFlights")
    suspend fun getFlights(
        @Query("fromId") fromId: String,
        @Query("toId") toId: String,
        @Query("departDate") departDate: String,
        @Query("currency_code") currencyCode: String = "PLN",
        @Query("returnDate") returnDate: String?,
        @Query("adults") adults: Int,
        @Query("children") children: String?,
        @Query("sort") sort: String,
        @Query("cabinClass") cabinClass: String = "ECONOMY"
    ): FlightSearchResponse

}