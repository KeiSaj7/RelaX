package com.example.relax.models.navigationRoutes

import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
object ResultsGraphRoute

@Serializable
data class FlightsRoute(
    val destinationName: String?,
    val departDate: String,
    val returnDate: String,
    val adults: Int,
    val children: String?
)

@Serializable
data class HotelsRoute(
    val destinationName: String?,
    val checkInDate: String,
    val checkOutDate: String,
    val adults: Int,
    val children: String?
)