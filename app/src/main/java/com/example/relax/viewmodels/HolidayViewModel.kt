package com.example.relax.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relax.models.Flights
import com.example.relax.models.network.APIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HolidayViewModel @Inject constructor(
    private val repository: APIRepository
) : ViewModel(){
    private val _startingLocation = MutableStateFlow<Flights?>(null)
    val startingLocation: StateFlow<Flights?> = _startingLocation
    private val _destination = MutableStateFlow<Flights?>(null)
    val destination: StateFlow<Flights?> = _destination


    fun getDestination(query: String, point: String){
        viewModelScope.launch {
            try{
                val result = repository.getDestination(query)
                Log.d("API_RESPONSE", "Success: $result")
                if(point == "start"){
                    _startingLocation.value = result
                    return@launch
                }
                _destination.value = result
            } catch (e: Exception){
                Log.e("API_ERROR", "Error: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}