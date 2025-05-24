package com.example.relax.models.endpoints

import com.google.gson.annotations.SerializedName

data class HotelDetailsResponse(
    @SerializedName("status") val status: Boolean?,
    @SerializedName("message") val message: String?,
    @SerializedName("timestamp") val timestamp: Long?,
    @SerializedName("data") val data: HotelDetailsData?
)

data class HotelDetailsData(
    @SerializedName("url") val url: String?,
)