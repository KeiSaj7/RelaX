package com.example.relax.models.endpoints.searchFlightLocation

data class Flight (
    val id: String,
    val type: String,
    val name: String,
    val code: String,
    val city: String,
    val cityName: String,
    val regionName: String,
    val country: String,
    val countryName: String,
    val countryNameShort: String,
    val photoUri: String,
    val distanceToCity: DistanceToCity,
    val parent: String
)