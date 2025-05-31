package com.example.relax.models.network

import com.example.relax.models.endpoints.AttractionLocation
import com.example.relax.models.endpoints.Attractions
import retrofit2.http.GET
import retrofit2.http.Query

interface AttractionsService {

    @GET("attraction/searchLocation")
    suspend fun getLocation(
        @Query("query") query: String,
        @Query("languagecode") languagecode: String = "pl"
    ): AttractionLocation

    @GET("attraction/searchAttractions")
    suspend fun getAttractions(
        @Query("id") id: String,
        @Query("languagecode") languagecode: String = "pl"
    ): Attractions
}