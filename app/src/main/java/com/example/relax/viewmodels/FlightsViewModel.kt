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

    val flights: StateFlow<FlightSearchResponse?> = repository.flightsSearchResponse

    val routeArgs: FlightsRoute = savedStateHandle.toRoute()

    fun navigateToHotels(navController: NavController) {
        navController.navigate(
            HotelsRoute(
                destinationName = routeArgs.destinationName,
                checkInDate = routeArgs.departDate!!,
                checkOutDate = routeArgs.returnDate!!,
                adults = routeArgs.adults!!,
                children = routeArgs.children
            )
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
        attractionsRepository.clearResponse()
        hotelsRepository.clearResponse()
        repository.clearResponse()
        navController.navigate(
            HomeRoute
        )
    }

}