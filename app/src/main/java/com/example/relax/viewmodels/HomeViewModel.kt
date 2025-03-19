package com.example.relax.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel: ViewModel() {
    private val _startingLocation = MutableStateFlow("")
    private val _destination = MutableStateFlow("")

    val startingLocation: StateFlow<String> = _startingLocation
    val destination: StateFlow<String> = _destination

    fun updateStartingLocation(newLocation: String){
        _startingLocation.value = newLocation
    }
    fun updateDestination(newDestination: String){
        _destination.value = newDestination
    }
}