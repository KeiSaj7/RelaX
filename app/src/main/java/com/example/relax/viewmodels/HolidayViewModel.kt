package com.example.relax.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relax.models.endpoints.searchFlightLocation.Flight
import com.example.relax.models.endpoints.searchFlightLocation.Flights
import com.example.relax.models.endpoints.searchFlights.FlightSearchResponse
import com.example.relax.models.network.APIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: APIRepository
) : ViewModel(){

    private val _startingLocation = MutableStateFlow<List<Flight>?>(null)
    val startingLocation: StateFlow<List<Flight>?> = _startingLocation
    private val _destination = MutableStateFlow<List<Flight>?>(null)
    val destination: StateFlow<List<Flight>?> = _destination
    private val _flights = MutableStateFlow<FlightSearchResponse?>(null)
    val flights: StateFlow<FlightSearchResponse?> = _flights


    fun getDestination(query: String, point: String){
        viewModelScope.launch {
            try{
                val result = repository.getDestination(query)
                Log.d("API_RESPONSE", "Success: $result")
                if(point == "start"){
                    _startingLocation.value = result.data
                    return@launch
                }
                _destination.value = result.data
            } catch (e: Exception){
                Log.e("API_ERROR", "Error: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun getFlights(
        fromId: String,
        toId: String,
        departDate: String,
        returnDate: String? = null,
        adults: Int = 1,
        children: String? = null,
        sort: String = "CHEAPEST",
    ){
        viewModelScope.launch {
            try{
                val result = repository.getFlights(
                    fromId = fromId,
                    toId = toId,
                    departDate = departDate,
                    returnDate = returnDate,
                    adults = adults,
                    children = children,
                    sort = sort,
                )
                Log.d("API_RESPONSE", "Success: $result")
                _flights.value = result
            } catch (e: Exception){
                Log.e("API_ERROR", "Error: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}