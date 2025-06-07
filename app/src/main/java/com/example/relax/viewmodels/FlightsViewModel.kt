package com.example.relax.viewmodels

import com.example.relax.models.navigationRoutes.FlightsRoute
import com.example.relax.models.navigationRoutes.HotelsRoute
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.example.relax.models.endpoints.FlightSearchResponse
import com.example.relax.models.navigationRoutes.AttractionsRoute
import com.example.relax.models.navigationRoutes.HomeRoute
import com.example.relax.models.network.AttractionsRepository
import com.example.relax.models.network.FlightsRepository
import com.example.relax.models.network.HotelsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FlightsViewModel @Inject constructor(
    private val repository: FlightsRepository,
    private val hotelsRepository: HotelsRepository,
    private val attractionsRepository: AttractionsRepository,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    init {
        repository.updateRouteArgs(savedStateHandle.toRoute())
    }
    val flights: StateFlow<FlightSearchResponse?> = repository.flightsSearchResponse

    val routeArgs: StateFlow<FlightsRoute?> = repository.routeArgs



    fun navigateToHotels(navController: NavController) {
        navController.navigate(
            HotelsRoute(
                destinationName = routeArgs.value!!.destinationName,
                checkInDate = routeArgs.value!!.departDate!!,
                checkOutDate = routeArgs.value!!.returnDate!!,
                adults = routeArgs.value!!.adults!!,
                children = routeArgs.value!!.children
            )
        )
    }

    fun navigateToAttractions(navController: NavController){
        navController.navigate(
            AttractionsRoute(
                destinationName = routeArgs.value!!.destinationName!!
            )

        )
    }

    fun navigateToHome(navController: NavController) {
        attractionsRepository.clearResponse()
        hotelsRepository.clearResponse()
        repository.clearResponse()
        navController.navigate(
            HomeRoute
        )
    }

}