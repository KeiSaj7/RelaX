package com.example.relax.viewmodels

import com.example.relax.models.navigationRoutes.HotelsRoute
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.example.relax.models.endpoints.HotelDetailsResponse
import com.example.relax.models.endpoints.SearchHotelsResponse
import com.example.relax.models.navigationRoutes.AttractionsRoute
import com.example.relax.models.navigationRoutes.FlightsRoute
import com.example.relax.models.navigationRoutes.HomeRoute
import com.example.relax.models.network.AttractionsRepository
import com.example.relax.models.network.FlightsRepository
import com.example.relax.models.network.HotelsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HotelsViewModel @Inject constructor(
    private val repository: HotelsRepository,
    private val flightsRepository: FlightsRepository,
    private val attractionsRepository: AttractionsRepository,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val routeArgs: HotelsRoute = savedStateHandle.toRoute()

    val hotels: StateFlow<SearchHotelsResponse?> = repository.cache

    val urlResponse: StateFlow<HotelDetailsResponse?> = repository.url

    private val _hotelDestId = MutableStateFlow<String?>(null)
    val hotelDestId: StateFlow<String?> = _hotelDestId.asStateFlow()

    var departDate: String? = ""

    init {
        if (hotels.value != null) {
            Log.d("RelaxLOG", "Hotels already fetched.")
        }
        else {
            getHotels()
        }
    }

    fun getHotels(){
       viewModelScope.launch {
           try{
               val destinationId = getDestinationId()

               if (destinationId == null){
                   _hotelDestId.value = null
                   Log.e("RelaxLOG", "Failed to get  destination ID for ${routeArgs.destinationName}")
               }
               val effectiveCheckOut: String?
               _hotelDestId.value = destinationId
                Log.d("RelaxLOG", "Success: $destinationId, ${routeArgs.checkInDate}, ${routeArgs.checkOutDate}, ${routeArgs.adults}, ${routeArgs.children}")
               if (routeArgs.checkOutDate.isBlank()){
                   Log.d("RelaxLOG", "checkOutDate is blank, setting to default date: ${routeArgs.checkInDate}")
                   departDate = calculateNextDay(routeArgs.checkInDate)
               }
               else{
                   departDate = routeArgs.checkOutDate
               }
                val response = repository.getHotels(
                    destId = destinationId,
                    arrivalDate = routeArgs.checkInDate,
                    departureDate = departDate.toString(),
                    adults = routeArgs.adults,
                    children = routeArgs.children
                )
               Log.d("RelaxLOG", "Success: $response")
               repository.insertIntoCache(response)
           }
           catch (e: Exception){
               Log.e("RelaxLOG", "Error in getHotels: ${e.message}, ${_hotelDestId.value}")
               e.printStackTrace()
           }
       }
    }

    suspend fun getHotelDetails(hotelId: String) {
        try{
            repository.getHotelDetails(
                hotelId = hotelId,
                arrivalDate = routeArgs.checkInDate,
                departureDate = departDate.toString(),
                adults = routeArgs.adults,
                children = routeArgs.children
            )
        }
        catch (e : Exception){
            e.printStackTrace()
            Log.e("RelaxLOG", "Error while fetching hotel url: ${e.message}")

        }
    }

    suspend fun getDestinationId(): String? {
        try{
            val hotelDestination = repository.getHotelDestination(query = routeArgs.destinationName)
            Log.d("RelaxLOG", "Success: $hotelDestination")
            val fetchedId = hotelDestination.data?.firstOrNull {it.searchType == "city"}?.destId
            return fetchedId
        }
        catch (e : Exception){
            e.printStackTrace()
            return null
        }
    }

    fun clearUrl(){
        repository.clearUrl()
    }

    private fun calculateNextDay(dateString: String, format: String = "yyyy-MM-dd"): String? {
        return try {
            val formatter = DateTimeFormatter.ofPattern(format, Locale.US)
            val date = LocalDate.parse(dateString, formatter)
            date.plusDays(1).format(formatter)
        } catch (e: Exception) {
            Log.e("RelaxLOG", "Error calculating next day for $dateString: ${e.message}")
            null
        }
    }

    fun navigateToFlights(navController: NavController) {
        navController.navigate(
            FlightsRoute()
        )
    }

    fun navigateToAttractions(navController: NavController){
        navController.navigate(
            AttractionsRoute(
                destinationName = routeArgs.destinationName!!
            )

        )
    }

    fun navigateToHome(navController: NavController) {
        repository.clearResponse()
        attractionsRepository.clearResponse()
        flightsRepository.clearResponse()
        navController.navigate(
            HomeRoute
        )
    }
}