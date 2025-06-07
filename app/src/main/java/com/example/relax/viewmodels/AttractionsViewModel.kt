package com.example.relax.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.example.relax.models.endpoints.Attraction
import com.example.relax.models.navigationRoutes.AttractionsRoute
import com.example.relax.models.navigationRoutes.FlightsRoute
import com.example.relax.models.navigationRoutes.HomeRoute
import com.example.relax.models.navigationRoutes.HotelsRoute
import com.example.relax.models.network.AttractionsRepository
import com.example.relax.models.network.FlightsRepository
import com.example.relax.models.network.HotelsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttractionsViewModel @Inject constructor(
    private val repository: AttractionsRepository,
    private val flightsRepository: FlightsRepository,
    private val hotelsRepository: HotelsRepository,
    savedStateHandle: SavedStateHandle
):ViewModel(){

    val routeArgs: StateFlow<AttractionsRoute?> = repository.routeArgs

    val attractions: StateFlow<List<Attraction>?> = repository.attractions

    val status: StateFlow<Boolean?> = repository.status

    private val _errorFlag = MutableStateFlow<Boolean>(false)
    val errorFlag: StateFlow<Boolean> = _errorFlag.asStateFlow()

    init {
        if(attractions.value != null){
            Log.d("RelaxLOG", "Attractions already fetched.")
        }
        else{
            repository.updateRouteArgs(savedStateHandle.toRoute())
            getAttractions()
        }
    }

    fun getAttractions(){
        viewModelScope.launch {
            try{
                Log.d("RelaxLOG", routeArgs.value!!.destinationName)
                repository.getLocation(routeArgs.value!!.destinationName)
                Log.d("RelaxLOG", "Attractions location fetched successful")
                repository.getAttractions()
                Log.d("RelaxLOG", "Attractions fetched successful")
            }
            catch (e: Exception){
                _errorFlag.value = true
                Log.e("RelaxLOG", "Error in getAttractions: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun changeFlag(status: Boolean){
        _errorFlag.value = status
    }

    fun navigateToHome(navController: NavController){
        repository.clearResponse()
        hotelsRepository.clearResponse()
        flightsRepository.clearResponse()
        navController.navigate(
            HomeRoute
        )
    }

    fun navigateToHotels(navController: NavController){
        navController.navigate(
            HotelsRoute(
                destinationName = hotelsRepository.routeArgs.value!!.destinationName,
                checkInDate = hotelsRepository.routeArgs.value!!.checkInDate,
                checkOutDate = hotelsRepository.routeArgs.value!!.checkOutDate,
                adults = hotelsRepository.routeArgs.value!!.adults,
                children = hotelsRepository.routeArgs.value!!.children
            )
        )
    }

    fun navigateToFlights(navController: NavController){
        navController.navigate(
            FlightsRoute(
                destinationName = flightsRepository.routeArgs.value!!.destinationName,
                departDate = flightsRepository.routeArgs.value!!.departDate,
                returnDate = flightsRepository.routeArgs.value!!.returnDate,
                adults = flightsRepository.routeArgs.value!!.adults,
                children = flightsRepository.routeArgs.value!!.children
            )
        )
    }

};