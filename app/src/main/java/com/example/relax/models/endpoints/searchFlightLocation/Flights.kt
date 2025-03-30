package com.example.relax.models.endpoints.searchFlightLocation

import com.google.gson.annotations.SerializedName

data class Flights (
    @SerializedName("status") val status: Boolean?,
    @SerializedName("message") val message: String?,
    @SerializedName("timestamp") val timestamp: Long?,
    @SerializedName("data") val data: List<Flight>?
)

data class Flight (
    @SerializedName("id") val id: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("code") val code: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("cityName") val cityName: String?,
    @SerializedName("regionName") val regionName: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("countryName") val countryName: String?,
    @SerializedName("countryNameShort") val countryNameShort: String?,
    @SerializedName("photoUri") val photoUri: String?,
    @SerializedName("distanceToCity") val distanceToCity: DistanceToCity?,
    @SerializedName("parent") val parent: String?
)

data class DistanceToCity (
    @SerializedName("value") val value: Float?,
    @SerializedName("unit") val unit: String?
)