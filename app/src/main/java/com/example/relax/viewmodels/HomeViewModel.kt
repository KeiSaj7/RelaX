package com.example.relax.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relax.models.endpoints.Flight
import com.example.relax.models.network.FlightsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FlightsRepository
) : ViewModel(){

    private val _startingLocation = MutableStateFlow<List<Flight>?>(null)
    val startingLocation: StateFlow<List<Flight>?> = _startingLocation.asStateFlow()

    private val _destination = MutableStateFlow<List<Flight>?>(null)
    val destination: StateFlow<List<Flight>?> = _destination.asStateFlow()

    private val _isSearchingFlights = MutableStateFlow(false)
    val isSearchingFlights: StateFlow<Boolean> = _isSearchingFlights.asStateFlow()


    fun clearLocationSuggestions(point: String) {
        if (point == "start") {
            _startingLocation.value = null
        } else {
            _destination.value = null
        }
    }

    fun getDestination(query: String, point: String){
        if (query.length < 2) {
            clearLocationSuggestions(point)
            return
        }
        viewModelScope.launch {
            try{
                val result = repository.getDestination(query)
                Log.d("RelaxLOG", "Success: $result")
                if(point == "start"){
                    _startingLocation.value = result.data
                }
                else{
                    _destination.value = result.data
                }
            } catch (e: Exception){
                Log.e("RelaxLOG", "Error in getDestination: ${e.message}")
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
                _isSearchingFlights.value = true
                repository.getFlights(
                    fromId = fromId,
                    toId = toId,
                    departDate = departDate,
                    returnDate = returnDate,
                    adults = adults,
                    children = children,
                    sort = sort,
                )
                Log.d("RelaxLOG", "Success: ${repository.flightsSearchResponse}")
            } catch (e: Exception){
                Log.e("RelaxLOG", "Error in getFlights: ${e.message}")
                e.printStackTrace()
            }
            finally {
            _isSearchingFlights.value = false
            }
        }
    }
}