package com.example.relax.models.network

import android.util.Log
import com.example.relax.models.endpoints.HotelDestinationResponse
import com.example.relax.models.endpoints.HotelDetailsResponse
import com.example.relax.models.endpoints.SearchHotelsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HotelsRepository @Inject constructor(private val hotelsService: HotelsService) {

    private val _cache = MutableStateFlow<SearchHotelsResponse?>(null)
    val cache: StateFlow<SearchHotelsResponse?> = _cache.asStateFlow()

    private val _url = MutableStateFlow<HotelDetailsResponse?>(null)
    val url: StateFlow<HotelDetailsResponse?> = _url.asStateFlow()

    fun clearResponse(){
        Log.d("RelaxLOG", "Hotels data cleared.")
        _cache.value = null
        _url.value = null
    }

    fun insertIntoCache(data: SearchHotelsResponse?){
        _cache.value = data
    }

    fun clearUrl(){
        _url.value = null
    }

    suspend fun getHotelDetails(
        hotelId: String,
        arrivalDate: String,
        departureDate: String,
        adults: Int,
        children: String?,
    ){
        _url.value = hotelsService.getHotelDetails(
            hotelId = hotelId,
            arrivalDate = arrivalDate,
            departureDate = departureDate,
            adults = adults,
            children = children,
        )
    }

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