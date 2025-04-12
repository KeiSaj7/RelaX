package com.example.relax.models.network

import com.example.relax.models.endpoints.Flights
import com.example.relax.models.endpoints.FlightSearchResponse
import com.example.relax.models.endpoints.HotelDestinationResponse
import com.example.relax.models.endpoints.SearchHotelsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface HotelsService {
    @GET("hotels/searchDestination")
    suspend fun getHotelDestination(
        @Query("query") query: String?
    ): HotelDestinationResponse

    @GET("hotels/searchHotels")
    suspend fun getHotels(
        @Query("dest_id") destId: String?,
        @Query("search_type") searchType: String? = "city",
        @Query("arrival_date") arrivalDate: String,
        @Query("departure_date") departureDate: String,
        @Query("adults") adults: Int,
        @Query("children") children: String?,
        @Query("room_qty") roomQuantity: Int? = 1,
        @Query("sort_by") sortBy: String? = "popularity",
        @Query("languagecode") languagecode: String = "pl",
        @Query("currency_code") currencyCode: String = "PLN",
    ): SearchHotelsResponse
}