package com.example.relax.models.endpoints

import com.google.gson.annotations.SerializedName

data class AttractionLocation(
    @SerializedName("status") val status: Boolean?,
    @SerializedName("message") val message: String?,
    @SerializedName("timestamp") val timestamp: Long?,
    @SerializedName("data") val data: LocationData?
)

data class LocationData(
    @SerializedName("destinations") val destinations: List<Destination>?
)

data class Destination(
    @SerializedName("id") val id: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("cityName") val cityName: String?
)