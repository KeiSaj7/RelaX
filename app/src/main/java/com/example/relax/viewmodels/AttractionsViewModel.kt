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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttractionsViewModel @Inject constructor(
    private val repository: AttractionsRepository,
    private val flightsRepository: FlightsRepository,
    private val hotelsRepository: HotelsRepository,
    savedStateHandle: SavedStateHandle
):ViewModel(){

    private val routeArgs: AttractionsRoute = savedStateHandle.toRoute()

    val attractions: StateFlow<List<Attraction>?> = repository.attractions

    init {
        if(attractions.value != null){
            Log.d("RelaxLOG", "Attractions already fetched.")
        }
        else{
            getAttractions()
        }
    }

    fun getAttractions(){
        viewModelScope.launch {
            try{
                repository.getLocation(routeArgs.destinationName)
                Log.d("RelaxLOG", "Attractions location fetched successful")
                repository.getAttractions()
                Log.d("RelaxLOG", "Attractions fetched successful")
            }
            catch (e: Exception){
                Log.e("RelaxLOG", "Error in getAttractions: ${e.message}")
                e.printStackTrace()
            }
        }
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
            HotelsRoute()
        )
    }

    fun navigateToFlights(navController: NavController){
        navController.navigate(
            FlightsRoute()
        )
    }

};