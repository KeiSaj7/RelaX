package com.example.relax.viewmodels

import com.example.relax.models.navigationRoutes.HotelsRoute
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.relax.models.endpoints.HotelDestinationId
import com.example.relax.models.endpoints.SearchHotelsResponse
import com.example.relax.models.network.HotelsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HotelsViewModel @Inject constructor(
    private val repository: HotelsRepository,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val routeArgs: HotelsRoute = savedStateHandle.toRoute()

    private val _hotels = MutableStateFlow<SearchHotelsResponse?>(null)
    val hotels: StateFlow<SearchHotelsResponse?> = _hotels.asStateFlow()

    private val _hotelDestId = MutableStateFlow<String?>(null)
    val hotelDestId: StateFlow<String?> = _hotelDestId.asStateFlow()

    //private val _isDataLoaded = MutableStateFlow<Boolean>(false)
    //val isDataLoaded: StateFlow<Boolean?> = _isDataLoaded.asStateFlow()

    init {
        //if(isDataLoaded.value == false){ }
        getHotels()

    }

    fun getHotels(){
       viewModelScope.launch {
           try{
               val destinationId = getDestinationId()

               if (destinationId == null){
                   _hotelDestId.value = null
                   Log.e("HotelsViewModel", "Failed to get  destination ID for ${routeArgs.destinationName}")
               }
               _hotelDestId.value = destinationId

                val response = repository.getHotels(
                    destId = destinationId,
                    arrivalDate = routeArgs.checkInDate,
                    departureDate = routeArgs.checkOutDate,
                    adults = routeArgs.adults,
                    children = routeArgs.children
                )
               Log.d("API_RESPONSE", "Success: $response")
               _hotels.value = response
               //_isDataLoaded.value = true
           }
           catch (e: Exception){
               Log.e("API_ERROR", "Error in getHotels: ${e.message}, ${_hotelDestId.value}")
               e.printStackTrace()
           }
       }
    }

    suspend fun getDestinationId(): String? {
        try{
            val hotelDestination = repository.getHotelDestination(query = routeArgs.destinationName)
            Log.d("API_RESPONSE", "Success: $hotelDestination")
            val fetchedId = hotelDestination.data?.firstOrNull {it.searchType == "city"}?.destId
            return fetchedId
        }
        catch (e : Exception){
            Log.e("API_ERROR", "Error in getDestinationId: ${e.message}")
            e.printStackTrace()
            return null
        }
    }

}