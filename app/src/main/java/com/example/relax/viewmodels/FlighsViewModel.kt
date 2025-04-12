package com.example.relax.viewmodels

import com.example.relax.models.navigationRoutes.FlightsRoute
import com.example.relax.models.navigationRoutes.HotelsRoute
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.example.relax.models.endpoints.FlightSearchResponse
import com.example.relax.models.network.FlightsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FlightsViewModel @Inject constructor(
    repository: FlightsRepository,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    val flights: StateFlow<FlightSearchResponse?> = repository.flightsSearchResponse

    val routeArgs: FlightsRoute = savedStateHandle.toRoute()

    fun navigateToHotels(navController: NavController) {
        navController.navigate(
            HotelsRoute(
                destinationName = routeArgs.destinationName,
                checkInDate = routeArgs.departDate,
                checkOutDate = routeArgs.returnDate,
                adults = routeArgs.adults,
                children = routeArgs.children
            )
        )
    }

}