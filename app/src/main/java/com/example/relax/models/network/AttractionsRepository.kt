package com.example.relax.models.network

import android.util.Log
import com.example.relax.models.endpoints.Attraction
import com.example.relax.models.navigationRoutes.AttractionsRoute
import com.example.relax.models.navigationRoutes.HotelsRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttractionsRepository @Inject constructor(private val attractionsService: AttractionsService){

    private val _id = MutableStateFlow<String?>(null)
    val id: StateFlow<String?> = _id.asStateFlow()

    private val _attractions = MutableStateFlow<List<Attraction>?>(null)
    val attractions: StateFlow<List<Attraction>?> = _attractions.asStateFlow()

    private val _status = MutableStateFlow<Boolean?>(null)
    val status: StateFlow<Boolean?> = _status.asStateFlow()

    private val _routeArgs = MutableStateFlow<AttractionsRoute?>(null)
    val routeArgs: StateFlow<AttractionsRoute?> = _routeArgs.asStateFlow()

    fun updateRouteArgs(args: AttractionsRoute?){
        _routeArgs.value = args
    }

    fun clearResponse(){
        Log.d("RelaxLOG", "Attractions data cleared.")
        _id.value = null
        _attractions.value = null
    }

    suspend fun getLocation( query: String )
    {
        _id.value = attractionsService.getLocation(query = query).data?.destinations?.get(0)?.id
    }

    suspend fun getAttractions()
    {
        val response = attractionsService.getAttractions(id.value!!)
        _attractions.value = response.data?.products
        _status.value = response.status
    }
}